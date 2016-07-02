package audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import utils.AppConstants;

/**
 * Created by Andrea on 18/04/2016.
 *
 * The class ManageAudio is used to play some audio effects
 * (e.g., when an user finds a new direction, WayFinder notifies it through a "beep".
 */
public class ManageAudio implements MediaPlayer.OnCompletionListener{
    private int[] tracks;
    private int currentTrack;
    private MediaPlayer mediaPlayer = null;
    private Context c;
    private static final String TAG_DEBUG = "MANAGE_AUDIO";

    public ManageAudio(Context context){
        currentTrack=0;
        c = context;
    }

    public ManageAudio(int[] traks, Context context){
        currentTrack=0;
        this.tracks = traks;
        c = context;
        mediaPlayer = MediaPlayer.create(c.getApplicationContext(), tracks[currentTrack]);
        //mediaPlayer.setOnCompletionListener(this);
       // mediaPlayer.start();
    }

    public void play(){
        if(mediaPlayer!=null) {
           // Toast.makeText(c.getApplicationContext(),"play", Toast.LENGTH_LONG).show();
            Log.d(AppConstants.TAG_DEBUG_APP+TAG_DEBUG,"play");
            mediaPlayer.start();
        }
    }

    public void stop(){
        if(mediaPlayer!=null){
            Toast.makeText(c.getApplicationContext(), "stop", Toast.LENGTH_LONG).show();
            mediaPlayer.stop();
            mediaPlayer=null;
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            Toast.makeText(c.getApplicationContext(),"pause", Toast.LENGTH_LONG).show();
            mediaPlayer.pause();
        }
    }

    public void onCompletion(MediaPlayer arg0) {
        arg0.release();
        if (currentTrack+1 < tracks.length) {
            currentTrack++;
            arg0 = MediaPlayer.create(c.getApplicationContext(), tracks[currentTrack]);
            arg0.setOnCompletionListener(this);
            arg0.start();
            mediaPlayer = arg0;
        }
    }

    public void setTracks(int[] tracks) { this.tracks = tracks; }

    public int[] getTracks() { return tracks; }

    public Context getC() { return c; }

    public int getCurrentTrack() { return currentTrack; }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
