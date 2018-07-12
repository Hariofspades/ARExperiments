
package com.hariofspades.augmentedimages.common;

import android.content.Context;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.ViewGroup;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.hariofspades.augmentedimages.R;

import java.util.concurrent.CompletableFuture;

public class AugmentedImageNode extends AnchorNode {

    private static final String TAG = "AugmentedImageNode";

    private AugmentedImage image;
    private static CompletableFuture<ViewRenderable> modelFuture;

    public AugmentedImageNode(Context context, int layout) {
        // Upon construction, start loading the modelFuture
        if (modelFuture == null) {
            modelFuture = ViewRenderable.builder().setView(context, layout)
                    .build();
        }
    }

    /**
     * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
     * created based on an Anchor created from the image.
     *
     * @param image captured by your camera
     */
    public void setImage(AugmentedImage image) {
        this.image = image;
        if (!modelFuture.isDone()) {
            CompletableFuture.allOf(modelFuture)
                    .thenAccept((Void aVoid) -> {
                setImage(image);
            }).exceptionally(throwable -> {
                Log.e(TAG, "Exception loading", throwable);
                return null;
            });
        }

        setAnchor(image.createAnchor(image.getCenterPose()));

        //ViewGroup.LayoutParams params = layout.getLayoutParams();

        Node node = new Node();

        Pose pose = Pose.makeTranslation(0.0f, 0.0f, 0.25f);

        node.setParent(this);
        node.setLocalPosition(new Vector3(pose.tx(), pose.ty(), pose.tz()));
//        node.setLocalRotation(new Quaternion(pose.qx(), pose.qy(), pose.qz(), pose.qw()));
        node.setLocalRotation(new Quaternion(90f, 0f, 0f, -90f));
        node.setRenderable(modelFuture.getNow(null));

    }

    public AugmentedImage getImage() {
        return image;
    }
}
