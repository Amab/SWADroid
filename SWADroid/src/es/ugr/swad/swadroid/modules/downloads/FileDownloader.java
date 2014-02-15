/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2012 Víctor Terrón <`echo vt2rron1iaa32s | tr 132 @.e`>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ugr.swad.swadroid.modules.downloads;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * ***************************** Usage example ***********************
 * The following code shows how to retrieve an image and display it:
 * <p/>
 * TextView tv = new TextView(this);
 * <p/>
 * try {
 * FileDownloader downloader = new FileDownloader(this.getFilesDir());
 * String url = "http://i0.kym-cdn.com/entries/icons/original/000/003/619/Untitled-1.jpg";
 * File path = downloader.get(url);
 * <p/>
 * BitmapFactory.Options options = new BitmapFactory.Options();
 * options.inSampleSize = 1;
 * final Bitmap b = BitmapFactory.decodeFile(path.getPath(), options);
 * ImageView imagenDescargada = new ImageView(this);
 * imagenDescargada.setImageBitmap(b);
 * setContentView(imagenDescargada);
 * <p/>
 * } catch (MalformedURLException e){
 * tv.setText("URL incorrecta");
 * setContentView(tv);
 * } catch (FileNotFoundException e){
 * tv.setText("la URL no existe");
 * setContentView(tv);
 * } catch (IOException e) {*
 * tv.setText("error en la conexión");
 * setContentView(tv);
 * }
 */

public class FileDownloader {

    private final File download_dir;

    /* The class constructor: receives as its single argument the path to
     * the directory to which files will be downloaded. You probably need
     * to use here the output of Context.getFilesDir() */
    public FileDownloader(File download_dir) {
        this.download_dir = download_dir;
    }

    /* Return the path to the directory where files are saved */
    File getDownloadDir() {
        return this.download_dir;
    }

    /* Return the filename of the file to which the URL refers, as discussed
     * here: [http://stackoverflow.com/q/605696/]. For example, for the URL
     * "http://www.mydomain.com/music/mozart.ogg", the String "mozart.ogg"
     * would be returned. Null is returned if the URL doesn't have a filename
     * (such as "http://example.com/" or "http://www.domain.com/folder/" */
    private static String getFilenameFromURL(String url) {
        int slashIndex = url.lastIndexOf("/");
        if (slashIndex == url.length() - 1)
            return null;
        else
            return url.substring(slashIndex + 1);
    }

    /* Download the file located at the given URL, save it to a temporary file
     * and returns its path. Note that we are responsible for the deletion of
     * the file when it is no longer needed. Throws:
     * - MalformedUrlException: if a malformed URL is given as parameter.
     * - IOException: most probably because the connection to the server fails.
     * - FileNotFoundException: if the URL points to a non-existent file or
     *                          to a directory - such as "www.ugr.es/" */
    public File get(String url_str)
            throws IOException {

        URL url = new URL(url_str);

		/* Open a connection to the URL and a buffered input stream */
        URLConnection ucon = url.openConnection();
        InputStream is = ucon.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
 
		/*  Read bytes to the buffer until there is nothing more to read(-1) */
        ByteArrayBuffer baf = new ByteArrayBuffer(50);
        int current;
        while ((current = bis.read()) != -1) {
            baf.append((byte) current);
        }

		/* The downloaded file will be saved to a temporary file, whose prefix
         * will be the basename of the file and the suffix its extension */
        String filename = FileDownloader.getFilenameFromURL(url.getPath());
        if (filename == null)
            throw new FileNotFoundException("URL does not point to a file");

        int lastSlashIndex = filename.lastIndexOf("/");
        int lastDotIndex = filename.lastIndexOf(".");
		
		/* Avoid StringIndexOutOfBoundsException from being thrown if the
		 * file has no extension (such as "http://www.domain.com/README" */
        String basename;
        String extension = null;

        if (lastDotIndex == -1)
            basename = filename.substring(lastSlashIndex + 1);
        else {
            basename = filename.substring(lastSlashIndex + 1, lastDotIndex);
            extension = filename.substring(lastDotIndex);
        }
		
		/* The prefix must be at least three characters long */
        if (basename.length() < 3)
            basename = "tmp";

        File output = File.createTempFile(basename, extension, this.getDownloadDir());
        //System.out.println("output: " + output.getPath());
		/* Convert the Bytes read to a String. */
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(baf.toByteArray());
        fos.close();

        return output;

    }
}
