package ru.android.cyfral.servisnik.model.adapter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import ru.android.cyfral.servisnik.R;

public class RepairRequestCategoryViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private final ImageView mArrowExpandImageView;
    private TextView mCategoryTextView;

    public RepairRequestCategoryViewHolder(View itemView) {
        super(itemView);
        mCategoryTextView = (TextView) itemView.findViewById(R.id.textview_section_header);

        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.iv_arrow_expand);
    }

    public void bind(RepairRequestCategory repairCategory) {
        mCategoryTextView.setText(repairCategory.getName());
        if (repairCategory.getName().contains("Просроченные")) {
            mCategoryTextView.setTextColor(Color.BLACK);
        } else if (repairCategory.getName().contains("Выполнить сегодня")) {
            mCategoryTextView.setTextColor(Color.parseColor("#CF1D1D"));
        } else if (repairCategory.getName().contains("Более одного дня")) {
            mCategoryTextView.setTextColor(Color.parseColor("#4F7AB4"));
        }
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);

        if (expanded) {
            mArrowExpandImageView.setRotation(ROTATED_POSITION);
        } else {
            mArrowExpandImageView.setRotation(INITIAL_POSITION);
        }

    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);

        RotateAnimation rotateAnimation;
        if (expanded) { // rotate clockwise
            rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        } else { // rotate counterclockwise
            rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        }

        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        mArrowExpandImageView.startAnimation(rotateAnimation);

    }
}