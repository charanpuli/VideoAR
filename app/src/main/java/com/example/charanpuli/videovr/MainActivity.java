package com.example.charanpuli.videovr;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

public class MainActivity extends AppCompatActivity {
    private ModelRenderable videorenderable;
    private float HEIGHT=1.25f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArFragment arFragment=(ArFragment)getSupportFragmentManager().findFragmentById(R.id.arfragment);
        ExternalTexture texture=new ExternalTexture();
        MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.video);

        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);

        ModelRenderable.builder()
                .setSource(this,R.raw.video_screen)
                .build().thenAccept(modelRenderable -> {
                    videorenderable=modelRenderable;
                    videorenderable.getMaterial().setExternalTexture("videoTexture",texture);
                    videorenderable.getMaterial().setFloat4("keyColor",new Color(0.01843f,1f,0.098f));

        });
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            AnchorNode anchorNode=new AnchorNode(hitResult.createAnchor());

            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
                texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
                    anchorNode.setRenderable(videorenderable);
                    texture.getSurfaceTexture().setOnFrameAvailableListener(null);
                });
            }
            else
                anchorNode.setRenderable(videorenderable);
            float height=mediaPlayer.getVideoHeight();
            float width=mediaPlayer.getVideoWidth();
            anchorNode.setLocalScale(new Vector3(
                    HEIGHT*(width/height),HEIGHT,1.0f
            ));

            arFragment.getArSceneView().getScene().addChild(anchorNode);
        });

    }
}
