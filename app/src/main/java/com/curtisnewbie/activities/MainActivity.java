package com.curtisnewbie.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.services.App;
import com.curtisnewbie.services.AuthService;
import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.services.ExecService;
import com.curtisnewbie.util.IntentUtil;

import javax.inject.Inject;

import static com.curtisnewbie.util.IntentUtil.hasIntentActivity;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Main {@code Activity} which is the login page.
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    private EditText pwInput;
    private EditText nameInput;
    private Button loginBtn;
    private TextView instructTv;

    @Inject
    protected ExecService es;
    @Inject
    protected AuthService authService;
    @Inject
    protected AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);

        // setup the layout for this activity
        setContentView(R.layout.activity_main);
        pwInput = this.findViewById(R.id.pwInput);
        nameInput = this.findViewById(R.id.nameInput);
        loginBtn = this.findViewById(R.id.loginBtn);
        instructTv = this.findViewById(R.id.instructTv);

        // create thread to prompt msg about whether the user should sign in or sign up
        es.submit(() -> {
            int msg;
            if (authService.isRegistered()) {
                msg = R.string.signin_msg;
            } else {
                msg = R.string.register_msg;
                instructTv.setText(R.string.register_textview);
            }
            MsgToaster.msgLong(this, msg);
        });
    }

    /**
     * when the button is clicked, the entered name and password is processed. This
     * is a listener method for loginBtn.
     *
     * @param view implicit view object
     */
    public void addOnButtonClick(View view) {
        String entName = nameInput.getText().toString().trim();
        String entPW = pwInput.getText().toString().trim();

        es.submit(() -> {
            String msg;
            if (authService.isRegistered()) {
                if (authService.login(entName, entPW) && authService.isAuthenticated()) {
                    msg = String.format("Welcome %s", entName);
                    navToImageList();
                } else {
                    msg = getString(R.string.account_incorrect_msg);
                }
            } else {
                if (authService.register(entName, entPW)) {
                    msg = String.format("Registration Successful, Welcome %s", entName);
                } else {
                    msg = getString(R.string.account_not_registered_msg);
                }
            }
            this.runOnUiThread(() -> {
                // Remove entered credential
                nameInput.setText("");
                pwInput.setText("");
                MsgToaster.msgShort(this, msg);
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // open github repo page for this app
        if (item.getItemId() == R.id.aboutMenuItem) {
            Intent openWebpageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_repo_url)));
            if (hasIntentActivity(this, openWebpageIntent)) {
                startActivity(openWebpageIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Navigates to ImageListActivity. Should only be called when the user is
     * authenticated.
     */
    private void navToImageList() {
        // navigates to ImageListActivity
        Intent intent = new Intent(this, ImageListActivity.class);
        startActivity(intent);
    }
}
