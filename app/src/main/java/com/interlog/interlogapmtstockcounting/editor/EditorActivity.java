package com.interlog.interlogapmtstockcounting.editor;

import android.app.AlertDialog;
//import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.interlog.interlogapmtstockcounting.R;
import com.thebluealliance.spectrum.SpectrumPalette;

public class EditorActivity extends AppCompatActivity implements EditorView {

    EditText et_itemname, et_quantity, et_location;
   // ProgressDialog progressDialog;
    SpectrumPalette palette;
    ProgressBar progressBar;

    EditorPresenter presenter;

    int id;
    String itemName, quantity, rackLocation;

    Menu actionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        et_itemname = findViewById(R.id.itemNm);
        et_quantity = findViewById(R.id.quantt);
        et_location = findViewById(R.id.racLoc);

        palette = findViewById(R.id.palette);

      /*  palette.setOnColorSelectedListener(
                clr -> color = clr
        ); */


//      Progress Dialog
        //progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Please wait...");

        progressBar = new ProgressBar(EditorActivity.this, null, android.R.attr.progressBarStyleSmall);

        presenter = new EditorPresenter(this);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        itemName = intent.getStringExtra("itemName");
        quantity = intent.getStringExtra("quantity");
        rackLocation = intent.getStringExtra("rackLocation");
        //color = intent.getIntExtra("color", 0); // example getIntExtra

        setDataFromIntentExtra();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        actionMenu = menu;

        if (id != 0) {
            actionMenu.findItem(R.id.edit).setVisible(true);
            actionMenu.findItem(R.id.delete).setVisible(true);
            actionMenu.findItem(R.id.save).setVisible(false);
            actionMenu.findItem(R.id.update).setVisible(false);
        } else {
            actionMenu.findItem(R.id.edit).setVisible(false);
            actionMenu.findItem(R.id.delete).setVisible(false);
            actionMenu.findItem(R.id.save).setVisible(true);
            actionMenu.findItem(R.id.update).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String itemName = et_itemname.getText().toString().trim();
        String quantity = et_quantity.getText().toString().trim();
        String rackLocation = et_location.getText().toString().trim();

       // int color = this.color;

        switch (item.getItemId()) {
            case R.id.save:
                //Save
                if (itemName.isEmpty()) {
                    et_itemname.setError("Please enter part desc");
                } else if (quantity.isEmpty()) {
                    et_quantity.setError("Please enter quantity");
                } else if (rackLocation.isEmpty()) {
                    et_location.setError("Please enter part no");
                } else {
                    presenter.saveNote(itemName, quantity, rackLocation);
                }
                return true;

            case R.id.edit:

                editMode();
                actionMenu.findItem(R.id.edit).setVisible(false);
                actionMenu.findItem(R.id.delete).setVisible(false);
                actionMenu.findItem(R.id.save).setVisible(false);
                actionMenu.findItem(R.id.update).setVisible(true);

                return true;

            case R.id.update:
                //Update

                /*if (itemName.isEmpty()) {
                    et_itemname.setError("Please enter part number");
                } else */
                    if (quantity.isEmpty()) {
                    et_quantity.setError("Please enter quantity");
                } else if (rackLocation.isEmpty()){
                    et_location.setError("Please enter part number");
                } else {
                    presenter.updateNote(id, itemName, quantity, rackLocation);
                }

                return true;

            case R.id.delete:

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Confirm !");
                alertDialog.setMessage("Are you sure?");
                alertDialog.setNegativeButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    presenter.deleteNote(id);
                });
                alertDialog.setPositiveButton("Cancel",
                        (dialog, which) -> dialog.dismiss());

                alertDialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

   @Override
   public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestSuccess(String message) {
        Toast.makeText(EditorActivity.this,
                message,
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish(); //back to main activity
    }

    @Override
    public void onRequestError(String message) {
        Toast.makeText(EditorActivity.this,
                message,
                Toast.LENGTH_SHORT).show();
    }

    private void setDataFromIntentExtra() {

        if (id != 0) {
            et_itemname.setText(itemName);
            et_quantity.setText(quantity);
            et_location.setText(rackLocation);

            getSupportActionBar().setTitle("Update Information");
            readMode();
        } else {
           // palette.setSelectedColor(getResources().getColor(R.color.white));
            //color = getResources().getColor(R.color.white);
            editMode();
        }

    }

    private void editMode() {
        et_itemname.setFocusableInTouchMode(true);
        et_quantity.setFocusableInTouchMode(true);
       // palette.setEnabled(true);
    }

    private void readMode() {
        et_itemname.setFocusableInTouchMode(false);
        et_quantity.setFocusableInTouchMode(false);
        et_itemname.setFocusable(false);
        et_quantity.setFocusable(false);
        //palette.setEnabled(false);
    }
}
