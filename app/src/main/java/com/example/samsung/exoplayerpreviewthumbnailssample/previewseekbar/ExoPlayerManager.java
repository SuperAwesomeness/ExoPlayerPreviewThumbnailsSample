/*
 * Copyright 2016 The Android Open Source Project
 * Copyright 2017 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.samsung.exoplayerpreviewthumbnailssample.previewseekbar;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.samsung.exoplayerpreviewthumbnailssample.glide.GlideApp;
import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.madhavanmalolan.ffmpegandroidlibrary.Controller;

import java.io.File;


public class ExoPlayerManager implements PreviewLoader {

    private ImageView imageView;

    private static String url;

    private Activity mActivity;

    private boolean firstTime = true;
    private String externalFilesDir;
    private Uri uri;
    private int duration;
    private File file;

    public ExoPlayerManager(Activity activity, ImageView imageView, String videoUrl) {
        this.imageView = imageView;
        url = videoUrl;
        this.mActivity = activity;
    }

    /**
     * @param currentPosition current playback position in milliseconds
     * @param max
     */
    @Override
    public void loadPreview(long currentPosition, long max) {
        duration = (int) (max / 1000);

        if (firstTime) {
            // kick off multiple AsyncTasks to fetch the thumbnails in a batch (ONLY initially)
            fetchThumbnails(duration);
        }
        firstTime = false;

        file = new File(externalFilesDir + "/thumbnail_out" + (int) (currentPosition / 1000) + ".png");
        // load the images
        uri = Uri.fromFile(file);
        GlideApp
                .with(mActivity)
                .load(uri)
                .thumbnail(0.1f)
                .fitCenter()
                .into(imageView)
                .waitForLayout();
    }

    private void fetchThumbnails(int duration) {
        String fileOut;
        for (int i = 0; i <= duration; i++) {
            externalFilesDir = String.valueOf(mActivity.getExternalFilesDir(null).getAbsoluteFile());
            fileOut = externalFilesDir + "/thumbnail_out" + i + ".png";
            new LongOperation(String.valueOf(i), fileOut).execute();

            // seems that ThreadPoolExecutor can cause errors in the native code
//            DownloadTask downloadTask = new DownloadTask(i, url, fileOut);
//            DownloadManager.getDownloadManager().runDownloadFile(downloadTask);
        }
    }

    private static class LongOperation extends AsyncTask<Void, Void, Void> {

        private String second;
        private String fileOut;

        LongOperation(String second, String fileOut){
            this.second = second;
            this.fileOut = fileOut;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i("AsyncTask", "started a new AsyncTask...");
//            String secondString = second[0];
//            String fileOut = second[1];

            File file = new File(fileOut);
            // first check the image already exists
            // if yes, then directly load the image
            // if not, then kick off the AsyncTask
            Log.i("AsyncTask", "second string: " + second);
            if (!file.exists()) {
                // ffmpeg -y -i https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4 -ss 00:00:01.000 -vframes 1 thumbnail_out.png
                // ffmpeg -y -i https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4 -ss 00:00:01.000 -vframes 1 scale=320:-1 thumbnail_out.png
                Controller.getInstance().run(new String[]{
                        "-y",
                        "-i",
                        url,
                        "-ss",
                        "00:00:" + second + ".000",
                        "-vframes",
                        "1",
                        "-vf",
                        "scale=320:-1",
                        fileOut
                });
            }

            return null;
        }
    }
}


