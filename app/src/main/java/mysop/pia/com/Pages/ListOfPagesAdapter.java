package mysop.pia.com.Pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mysop.pia.com.Pages.PagesRoom.StepsRoomData;
import mysop.pia.com.R;

public class ListOfPagesAdapter extends RecyclerView.Adapter<ListOfPagesAdapter.Viewholder> {

    private List<StepsRoomData> listOfSteps;
    private Context context;
    public static int stepId;


    ListOfPagesAdapter(Context context, List<StepsRoomData> listOfSteps) {
        this.context = context;
        this.listOfSteps = listOfSteps;
    }

    @Override
    public void onBindViewHolder(@NonNull ListOfPagesAdapter.Viewholder viewholder, int position) {

//        SHOWS STEP TITLE
        viewholder.textviewStepTitle.setText(listOfSteps.get(position).getStepTitle());
        attachments(position, viewholder);

        viewholder.constraintListOfSteps.setOnClickListener(v -> {
            Intent goToStep = new Intent(context, PageActivity.class);
            goToStep.putExtra("position", position);
            stepId = listOfSteps.get(position).getId();
            context.startActivity(goToStep);
            ((Activity)context).finish();
        });
    }

    private void attachments(int position, Viewholder viewholder) {
        if (listOfSteps.get(position).getImageURI() != null || !listOfSteps.get(position).getStepDescription().equals("")) {
            viewholder.imageviewAttachment.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public ListOfPagesAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_of_steps_layout, viewGroup, false);
        return new Viewholder(view);
    }

    @Override
    public int getItemCount() {
        return listOfSteps.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_step_list_title)
        TextView textviewStepTitle;
        @BindView(R.id.constrain_layout_list_step)
        ConstraintLayout constraintListOfSteps;
        @BindView(R.id.imageView_step_list_attachment)
        ImageView imageviewAttachment;

        Viewholder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
