// Generated by view binder compiler. Do not edit!
package com.example.geosnapper.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.geosnapper.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMapBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final View VerticalLine1;

  @NonNull
  public final View VerticalLine2;

  @NonNull
  public final View VerticalLine3;

  @NonNull
  public final Button buttonTest1;

  @NonNull
  public final Button buttonTest2;

  @NonNull
  public final FragmentContainerView map;

  @NonNull
  public final RadioGroup radioGroupListSelector;

  private ActivityMapBinding(@NonNull FrameLayout rootView, @NonNull View VerticalLine1,
      @NonNull View VerticalLine2, @NonNull View VerticalLine3, @NonNull Button buttonTest1,
      @NonNull Button buttonTest2, @NonNull FragmentContainerView map,
      @NonNull RadioGroup radioGroupListSelector) {
    this.rootView = rootView;
    this.VerticalLine1 = VerticalLine1;
    this.VerticalLine2 = VerticalLine2;
    this.VerticalLine3 = VerticalLine3;
    this.buttonTest1 = buttonTest1;
    this.buttonTest2 = buttonTest2;
    this.map = map;
    this.radioGroupListSelector = radioGroupListSelector;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMapBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMapBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_map, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMapBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.VerticalLine1;
      View VerticalLine1 = ViewBindings.findChildViewById(rootView, id);
      if (VerticalLine1 == null) {
        break missingId;
      }

      id = R.id.VerticalLine2;
      View VerticalLine2 = ViewBindings.findChildViewById(rootView, id);
      if (VerticalLine2 == null) {
        break missingId;
      }

      id = R.id.VerticalLine3;
      View VerticalLine3 = ViewBindings.findChildViewById(rootView, id);
      if (VerticalLine3 == null) {
        break missingId;
      }

      id = R.id.buttonTest1;
      Button buttonTest1 = ViewBindings.findChildViewById(rootView, id);
      if (buttonTest1 == null) {
        break missingId;
      }

      id = R.id.buttonTest2;
      Button buttonTest2 = ViewBindings.findChildViewById(rootView, id);
      if (buttonTest2 == null) {
        break missingId;
      }

      id = R.id.map;
      FragmentContainerView map = ViewBindings.findChildViewById(rootView, id);
      if (map == null) {
        break missingId;
      }

      id = R.id.radio_group_list_selector;
      RadioGroup radioGroupListSelector = ViewBindings.findChildViewById(rootView, id);
      if (radioGroupListSelector == null) {
        break missingId;
      }

      return new ActivityMapBinding((FrameLayout) rootView, VerticalLine1, VerticalLine2,
          VerticalLine3, buttonTest1, buttonTest2, map, radioGroupListSelector);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
