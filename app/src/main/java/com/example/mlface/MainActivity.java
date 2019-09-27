package com.example.mlface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mlface.Helper.GraphicOverlay;
import com.example.mlface.Helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    CameraView cameraView;
    GraphicOverlay graphicOverlay;
    Button btndetect;
    //AlertDialog waitDialog;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        // cameraView.start();
        cameraView.captureImage();
        graphicOverlay.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraView.start();
        // cameraView.start();
        cameraView.captureImage();
        graphicOverlay.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init View
        cameraView = (CameraView) findViewById(R.id.camera_view);

        graphicOverlay = (GraphicOverlay) findViewById(R.id.graphic_overlay);
        btndetect = (Button) findViewById(R.id.btn);


            //waitDialog=new SpotsDialog.Builder().setContext(this).setMessage("Please Wait").setCancelable(false).build();

            //event
            btndetect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cameraView.start();
                    cameraView.captureImage();
                    graphicOverlay.clear();
                }
            });
            cameraView.addCameraKitListener(new CameraKitEventListener() {
                @Override
                public void onEvent(CameraKitEvent cameraKitEvent) {

                }

                @Override
                public void onError(CameraKitError cameraKitError) {

                }

                @Override
                public void onImage(CameraKitImage cameraKitImage) {
                    //waitDialog.show();

                    Bitmap bitmap = cameraKitImage.getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), true);
                    //
                    cameraView.start();

                    runFaceDetector(bitmap);

                }

                @Override
                public void onVideo(CameraKitVideo cameraKitVideo) {

                }
            });
        }

        private void runFaceDetector (Bitmap bitmap){
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder().build();

            FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                @Override
                public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                    processFaceResult(firebaseVisionFaces);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void processFaceResult (List < FirebaseVisionFace > firebaseVisionFaces) {
            int count = 0;
            for (FirebaseVisionFace face : firebaseVisionFaces) {
                Rect bounds = face.getBoundingBox();
                //Draw Rectangles
                RectOverlay rect = new RectOverlay(graphicOverlay, bounds);
                graphicOverlay.add(rect);
                count++;
            }
            //waitDialog.dismiss();


            Toast.makeText(this, String.format("Detected %d faces in image", count), Toast.LENGTH_SHORT).show();
        }


}
