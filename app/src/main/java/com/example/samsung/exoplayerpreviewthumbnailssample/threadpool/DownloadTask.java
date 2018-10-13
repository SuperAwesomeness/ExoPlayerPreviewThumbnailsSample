package com.example.samsung.exoplayerpreviewthumbnailssample.threadpool;

import com.madhavanmalolan.ffmpegandroidlibrary.Controller;

import java.io.File;

public class DownloadTask implements Runnable{
    String url;
    String fileOut;
    int second;
//    DownloadResultUpdateTask resultUpdateTask;

    public DownloadTask(int second, String urlIn, String fileOut/*,
                        DownloadResultUpdateTask drUpdateTask*/){
        this.second = second;
        this.url = urlIn;
        this.fileOut = fileOut;
//        resultUpdateTask = drUpdateTask;
    }

    @Override
    public void run() {
        downloadFile();
//        String msg;
//        if(downloadFile()){
//            msg = "file downloaded successful "+url;
//        }else{
//            msg = "failed to download the file "+url;
//        }
        //update results download status on the main thread
//        resultUpdateTask.setBackgroundMsg(msg);
//        DownloadManager.getDownloadManager().getMainThreadExecutor()
//                .execute(resultUpdateTask);
    }

    public boolean downloadFile(){
        try{
            // task
            File file = new File(fileOut);
            // first check the image already exists
            // if yes, then directly load the image
            // if not, then kick off the AsyncTask
//            Log.i("AsyncTask", "second string: " + second);
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
        }catch (Exception e){e.printStackTrace(); return false;}
        return true;
    }
}
