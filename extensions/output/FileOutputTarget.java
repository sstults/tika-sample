package com.olytech.tika.extensions.output;

import org.apache.tika.metadata.Metadata;

import java.io.*;
import java.util.UUID;

public class FileOutputTarget implements MetadataListener {

	private File outputDir = null;
	public FileOutputTarget(String outputTargetPath) {
		outputDir = new File(outputTargetPath);
		outputDir.mkdirs();
	}

	@Override
	public void handle(Metadata metadata) {
		String filename = (String) metadata.get("id");
		System.out.println("finished processing document " + filename);
		if (filename == null || filename.isEmpty()) {
			filename = "noid_" + UUID.randomUUID();
		}
		// unique filename
		File file = null;
		boolean exists = false;
		int i = 0;
		do {
			String name = filename;
			if (i > 0)
				name += "_" + i;
			// NOTE: This is not json.
			// TODO: See TIKA-213 for suggestions, or google-gson.
			file = new File(outputDir.getPath() + File.separator + name
					+ ".json");

			if (file.exists()) {
				exists = true;
				i++;
			} else {
				exists = false;
			}
		} while (exists);
        try {
    		FileOutputStream fos = new FileOutputStream(file);
	    	Writer out = new OutputStreamWriter(fos,"UTF8");
		    out.write(metadata.toString());
    		out.flush();
	    	out.close();
        } catch (IOException ioe) {
            System.out.println("IO ERROR writing to file:" + ioe.getLocalizedMessage());
        }
	}

    @Override
    public void allDone() {
        // nothing to do here.
    }

}
