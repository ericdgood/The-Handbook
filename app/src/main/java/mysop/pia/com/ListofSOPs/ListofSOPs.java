package mysop.pia.com.ListofSOPs;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mysop.pia.com.Categories.CategoryRecyclerAdapter;
import mysop.pia.com.R;
import mysop.pia.com.ListofSOPs.SopRoom.SOPRoomData;
import mysop.pia.com.ListofSOPs.SopRoom.SopAppDatabase;

public class ListofSOPs extends AppCompatActivity {

    @BindView(R.id.recyclerview_list_of_sops)
    RecyclerView recyclerviewListofSOPs;
    @BindView(R.id.listsop_categorytitle)
    TextView textviewCategoryListTitle;
    @BindView(R.id.fab_addsop)
    FloatingActionButton fabAddSOP;

    List<SOPRoomData> listOfSOPs = new ArrayList<>();
    String sopTitleDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_sops);
        ButterKnife.bind(this);

        setUpPage();

        setupRecyclerviewAndAdapter();

        fabAddSOP.setOnClickListener(v -> {
            Intent addNewSOP = new Intent(this, AddSOP.class);
            startActivity(addNewSOP);
        });
////        SWIPE TO DELETE
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
//                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
//                sopRoomDatabase().listOfSOPs().deleteSOP(setupRecyclerviewAndAdapter());
//                Toast.makeText(ListofSOPs.this, sopTitleDelete +" has been deleted", Toast.LENGTH_SHORT).show();
//            }
//        }).attachToRecyclerView(recyclerviewListofSOPs);

    }

    private void setUpPage() {
        String titleConcat = "List of " + CategoryRecyclerAdapter.categoryName + " SOPs";
        textviewCategoryListTitle.setText(titleConcat);
    }

    private void setupRecyclerviewAndAdapter(){
        listOfSOPs = sopRoomDatabase().listOfSOPs().getAllSOPsPerCat(CategoryRecyclerAdapter.categoryName);
        ListofSOPsAdapter SOPsRecyclerAdapter = new ListofSOPsAdapter(this, listOfSOPs);
        recyclerviewListofSOPs.setLayoutManager(new LinearLayoutManager(this));
        recyclerviewListofSOPs.setAdapter(SOPsRecyclerAdapter);
    }

    public SopAppDatabase sopRoomDatabase() {
        return Room.databaseBuilder(getApplicationContext(), SopAppDatabase.class, "sopinfo")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }
}
