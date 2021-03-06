package mysop.pia.com.ListofHandbooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mysop.pia.com.Firebase.Firebase;
import mysop.pia.com.Pages.ListOfPages;
import mysop.pia.com.Pages.PagesRoom.StepsAppDatabase;
import mysop.pia.com.Pages.PagesRoom.StepsRoomData;
import mysop.pia.com.R;

//import mysop.pia.com.Firebase.Firebase;

public class ListofHandbooksAdapter extends RecyclerView.Adapter<ListofHandbooksAdapter.Viewholder> {

    private final StepsAppDatabase db;
    private List<StepsRoomData> listOfSOPS;
    private Context context;
    public static int savedBook;
    public static String bookColor;
    public static int bookShare = 0;
    public static String bookTitle;

    FirebaseUser user;

    ListofHandbooksAdapter(Context context, List<StepsRoomData> listOfSOPs, StepsAppDatabase stepsAppDatabase) {
        this.context = context;
        this.listOfSOPS = listOfSOPs;
        this.db = stepsAppDatabase;
    }

    @Override
    public void onBindViewHolder(@NonNull ListofHandbooksAdapter.Viewholder viewholder, int position) {
        viewholder.tvBookTitle.setText(listOfSOPS.get(position).getSopTitle());

        viewholder.imgHandbook.setColorFilter(bookColor(position));

        viewholder.constrainBookList.setOnClickListener(v -> {
            Intent listOfSteps = new Intent(context, ListOfPages.class);
            bookTitle = listOfSOPS.get(position).getSopTitle();
            context.startActivity(listOfSteps);
        });

//        BOOK OPTIONS
            bookOptions(viewholder, position);

//      BOOKMARK BOOKS
        savedBook = listOfSOPS.get(position).getSavedBook();
        if (savedBook == 1) {
            viewholder.imgBookSave.setImageResource(R.drawable.ic_bookmark);
        }
        saveBook(viewholder, position, listOfSOPS.get(position).getSopTitle());
    }

    private int bookColor(int position) {
        bookColor = listOfSOPS.get(position).getBookColor();
        if (bookColor != null) {
            switch (bookColor) {
                case "High":
                    return context.getColor(R.color.logoRedBookColor);
                case "Low":
                    return context.getColor(R.color.logoBlueBookColor);
                case "Medium":
                    return context.getColor(R.color.logoGreenBookColor);
                default:
            }
        }
        return context.getColor(R.color.logoBlueBookColor);
    }

    @NonNull
    @Override
    public ListofHandbooksAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.books_list_layout, viewGroup, false);
        return new Viewholder(view);
    }

    private void saveBook(Viewholder viewholder, int position, String sopTitle) {
        viewholder.imgBookSave.setOnClickListener(v -> {
            if (savedBook == 1) {
//            UNSAVE BOOK
                savedBook = 0;
                viewholder.imgBookSave.setImageResource(R.drawable.baseline_bookmark_border_black_36dp);
                db.listOfSteps().updateBookSaved(0, bookTitle);
                Toast.makeText(context, sopTitle + context.getString(R.string.unbookmarked), Toast.LENGTH_SHORT).show();
            } else if (savedBook == 0) {
//            SAVE BOOK
                savedBook = 1;
                viewholder.imgBookSave.setImageResource(R.drawable.ic_bookmark);
                db.listOfSteps().updateBookSaved(1, bookTitle);
                Toast.makeText(context, sopTitle + context.getString(R.string.bookmarked), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookOptions(Viewholder viewholder, int position) {

        if (listOfSOPS.get(position).getSharedStatus() == 4){
            viewholder.imgBookShared.setVisibility(View.VISIBLE);
        }

        if (listOfSOPS.get(position).getSharedStatus() == 2 || listOfSOPS.get(position).getSharedStatus() == 5) {
//            SHOW THIS IS RECIEVED SHARED BOOK
            viewholder.imgBookSave.setVisibility(View.GONE);
            viewholder.imgBookShared.setVisibility(View.VISIBLE);
            viewholder.tvBookSharedBy.setVisibility(View.VISIBLE);
            viewholder.tvBookSharedByAuthor.setText(listOfSOPS.get(position).getSharedAuthor());
            viewholder.tvBookSharedByAuthor.setVisibility(View.VISIBLE);
        }

        if (listOfSOPS.get(position).getSharedStatus() == 0 ||
                listOfSOPS.get(position).getSharedStatus() == 4 ||
                listOfSOPS.get(position).getSharedStatus() == 1) {
            viewholder.imgBookOptions.setOnClickListener(v -> {
//            OPEN OPTIONS
                PopupMenu popup = new PopupMenu(context, viewholder.imgBookOptions);
                popup.inflate(R.menu.menu_book_shelf);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.book_shelf_edit:
                            editBook(listOfSOPS.get(position).getSopTitle(), listOfSOPS.get(position).getId(),
                                    listOfSOPS.get(position).getBookColor());
                            return true;
                        case R.id.book_shelf_collab:
                            Intent shareBook = new Intent(context, Firebase.class);
                            bookShare = 1;
                            bookTitle = listOfSOPS.get(position).getSopTitle();
                            context.startActivity(shareBook);
                            return true;
                        case R.id.book_shelf_delete:
                            alertToDelete(listOfSOPS.get(position).getSopTitle(), position);
                            return true;
                        default:
                            return false;
                    }
                });
                //displaying the popup
                popup.show();
            });
        }else {
            viewholder.imgBookOptions.setVisibility(View.GONE);
        }
        }

    private void editBook(String sopTitle, int id, String bookColor) {
        Intent editSOP = new Intent(context, AddHandbook.class);
        editSOP.putExtra(context.getString(R.string.editbook), 1);
        editSOP.putExtra(context.getString(R.string.editbooktitle), sopTitle);
        editSOP.putExtra(context.getString(R.string.editbookid), id);
        editSOP.putExtra("bookColor", bookColor);
        context.startActivity(editSOP);
        ((Activity) context).finish();
    }

    private void alertToDelete(String bookTitle, int position) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle(R.string.deletedentry)
                .setMessage(context.getString(R.string.rusuredelete) + bookTitle + context.getString(R.string.quesionmark))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // continue with delete
                    db.listOfSteps().DeleteSOP(bookTitle);
                    Toast.makeText(context, bookTitle + context.getString(R.string.isdeleted), Toast.LENGTH_SHORT).show();
                    listOfSOPS.remove(position);
                    notifyDataSetChanged();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                })
                .setIcon(R.drawable.ic_delete)
                .show();
//        return position;
    }

    @Override
    public int getItemCount() {
        return listOfSOPS.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        //        BINDVIEWS HERE
        @BindView(R.id.text_booklist_title)
        TextView tvBookTitle;
        @BindView(R.id.constrain_booklist_layout)
        ConstraintLayout constrainBookList;
        @BindView(R.id.image_booklist_saved)
        ImageView imgBookSave;
        @BindView(R.id.image_booklist_options)
        ImageView imgBookOptions;
        @BindView(R.id.imageview_handbook)
        ImageView imgHandbook;
        @BindView(R.id.image_booklist_shared)
        ImageView imgBookShared;
        @BindView(R.id.book_shared_by)
        TextView tvBookSharedBy;
        @BindView(R.id.book_shared_by_author)
        TextView tvBookSharedByAuthor;

        Viewholder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
