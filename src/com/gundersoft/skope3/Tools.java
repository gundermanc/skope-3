package com.gundersoft.skope3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Miscellaneous Methods and classes that came in handy
 * when building Skope but don't belong anywhere else.
 * @author Christian Gunderman
 */
public abstract class Tools {
	
    /**
     * Deletes a directory and all of its contents.
     * @param file The directory to delete.
     */
    public static boolean recursiveDelete(File file) {
	File[] contents = file.listFiles();
	if(contents != null) {
	    for(File f : contents)
		recursiveDelete(f);
	}
	return file.delete();
    }
	
    /**
     * Attempts to lock the lock file. If unable to do so, there is another 
     * instance already running.
     * @param lockFile File to use as lock file
     * @return Returns true if locked successfully, and false if not.
     */
    public static boolean lockInstance(final String lockFile) {
	try {
	    final File file = new File(lockFile);
	    final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
	    final FileLock fileLock = randomAccessFile.getChannel().tryLock();
	    if (fileLock != null) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
	                public void run() {
	                    try {
	                        fileLock.release();
	                        randomAccessFile.close();
	                        file.delete();
	                    } catch (IOException e) {
	                    }
	                }
	            });
		return true;
	    }
	} catch(IOException e) {
	}
	return false;
    }
	
    /**
     * Wraps standard Java Zip libraries to make them a bit
     * more user friendly and simple.
     */
    public static class ZipWrapper {
	/** Output stream of the current zip file. */
	private ZipOutputStream out;
		
	/**
	 * Creates the ZipWrapper object with the given file name.
	 * @param outputFile File to write to.
	 * @throws FileNotFoundException Thrown if file path does not
	 * exist.
	 */
	public ZipWrapper(File outputFile) throws FileNotFoundException {
	    this.out = new ZipOutputStream(new FileOutputStream(outputFile));
	    this.out.setLevel(9);
	}
		
	/**
	 * Closes the zip file and saves all changes.
	 * @throws IOException Thrown if the file cannot be 
	 * modified for some reason. 
	 */
	public void close() throws IOException {
	    this.out.close();
	}
		
	/**
	 * Adds a file to the current zip file.
	 * @param inputFile The file to add to the archive.
	 * @param zippedName The added file's name/path in the archive.
	 * @throws IOException Thrown if input file cannot be read.
	 */
	public void putFile(File inputFile, String zippedName) throws IOException {
	    FileInputStream in = new FileInputStream(inputFile);
	    this.out.putNextEntry(new ZipEntry(zippedName));
			
	    byte[] b = new byte[1024];
	    int count;
	        
	    while ((count = in.read(b)) > 0) {
		this.out.write(b, 0, count);
	    }
	    in.close();
	}
		
	/**
	 * Packs the specified file/directory and its contents
	 * into this zip archive in a single operation.
	 * @param directory The directory to pack.
	 * @throws IOException Thrown if the input files cannot
	 * be read.
	 */
	public void putDirectory(File directory) throws IOException {
	    File[] files = directory.listFiles();
			
	    for(File f : files) {
		if(f.isDirectory()) {
		    putDirectory(f);
		}
		else
		    this.putFile(f, f.getPath());
	    }
			
	}
    }
}
