package com.curtisnewbie.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.Image;
import com.curtisnewbie.services.App;
import com.curtisnewbie.services.ExecService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Adapter that manages items in {@code RecyclerView}
 * </p>
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    /**
     * string for Intent.putExtra() when navigating to imageViewActivity
     */
    public static final String IMG_NAME = "img_title";
    private List<String> imageNames;
    private Context context;
    @Inject
    protected AppDatabase db;
    @Inject
    protected ExecService es;
    private String imgKey;

    public ImageListAdapter(Context context, String imgKey) {
        App.getAppComponent().inject(this);
        this.imgKey = imgKey;
        this.context = context;
        this.imageNames = Collections.synchronizedList(new ArrayList<>());
        this.loadImageNamesFromDb();
    }

    // this method is for inflating the view of each item.
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.acitivity_each_item,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // loading resources for each ViewHolder
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        // setup the name for each image item
        holder.getName().setText(imageNames.get(position));

        // setup the onClickListener for the layout of whole Recycler layout
        holder.getItem_layout().setOnClickListener(view -> {
            Intent intent = new Intent(".ImageViewActivity");
            intent.putExtra(IMG_NAME, imageNames.get(holder.getAdapterPosition()));
            context.startActivity(intent);
        });

        // long click (hold) to create dialog for deleting the encrypted image
        holder.getItem_layout().setOnLongClickListener(e -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setMessage("Want to delete this image?")
                    .setPositiveButton("Yes", (dia, id) -> {
                        es.submit(() -> {
                            String msg;
                            int index = holder.getAdapterPosition();
                            String name = imageNames.get(index);
                            Image img = this.db.imgDao().getImage(name);
                            if (img != null && deleteImageFile(img.getPath())) {
                                // only update the RecyclerView when the file is actually deleted
                                this.deleteImage(index);
                                this.db.imgDao().deleteImage(img);
                                msg = String.format("%s deleted.", name);
                            } else {
                                msg = "File cannot be deleted, please try again";
                            }
                            ((Promptable) this.context).prompt(msg);
                        });
                    })
                    .setNegativeButton("No", (dia, id) -> {
                        // do nothing
                    });
            AlertDialog dia = builder.create();
            dia.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return imageNames.size();
    }

    /**
     * Load the whole list of image names from db in a separate {@code Thread}
     */
    private void loadImageNamesFromDb() {
        es.submit(() -> {
            this.imageNames.clear();
            this.imageNames.addAll(db.imgDao().getImageNames());
        });
    }

    /**
     * Insert a image name to the end of the list
     *
     * @param name image name
     */
    public void addImageName(String name) {
        this.imageNames.add(name);
        this.notifyItemInserted(imageNames.size() - 1);
    }

    /**
     * Delete an image from ths list
     *
     * @param index index in the {@code imageNames}
     * @return the name of the image being deleted
     */
    public void deleteImage(int index) {
        this.imageNames.remove(index);
        this.notifyItemRemoved(index);
    }

    /**
     * Delete a file
     *
     * @param path path to file
     * @return whether the file is deleted
     */
    private boolean deleteImageFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    // each view holder holds data of each item
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private RelativeLayout item_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTextView);
            item_layout = itemView.findViewById(R.id.item_layout);
        }

        public TextView getName() {
            return this.name;
        }

        public RelativeLayout getItem_layout() {
            return this.item_layout;
        }
    }
}
