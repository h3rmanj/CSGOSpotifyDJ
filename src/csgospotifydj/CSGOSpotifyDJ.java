package csgospotifydj;

import com.melloware.jintellitype.JIntellitype;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Scanner;

public class CSGOSpotifyDJ
{
    public static void main (String[] args)
    {
        new CSGOSpotifyDJ();
    }

    private JIntellitype intellitype = JIntellitype.getInstance();

    public CSGOSpotifyDJ ()
    {
        registerHotKey();
        intellitype.addHotKeyListener(i -> {
            intellitype.unregisterHotKey(1);
            if (i == 1)
            {
                String song = getCurrentSongPlaying();
                if (song != null && song.length() > 0)
                    printSongToCSGO(song);
                else
                    registerHotKey();
            }
        });
    }

    private void registerHotKey ()
    {
        intellitype.registerHotKey(1, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, (int)'V');
    }
    private synchronized String getCurrentSongPlaying ()
    {
        try
        {
            Process p = Runtime.getRuntime().exec("tasklist /fi \"IMAGENAME eq spotify.exe\" /fi \"STATUS ne Not Responding\" /v /nh /fo csv");
            Scanner in = new Scanner(p.getInputStream());

            String line;
            while (in.hasNext())
            {
                line = in.nextLine();
                if (!line.trim().equals(""))
                {
                    String[] lines = line.split(",");
                    String song = lines[8].replace("\"", "");
                    System.out.println(song);
                    return song;
                }
            }
            return null;
        }
        catch (IOException e) { return null; }
    }

    private synchronized void printSongToCSGO (String song)
    {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(song);
        clipboard.setContents(tText, null);

        try
        {
            Robot robot = new Robot();

            //Alltalk in CSGO
            robot.keyPress(KeyEvent.VK_Y);
            robot.keyRelease(KeyEvent.VK_Y);

            try { Thread.sleep(50); }
            catch (InterruptedException e) { e.printStackTrace(); }

            //Paste songname
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_V);

            //Send message
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }
        catch (AWTException e) { e.printStackTrace(); }
        finally { registerHotKey(); }
    }
}
