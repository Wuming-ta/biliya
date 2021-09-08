/**
 * 
 */
package com.jfinal.ext.plugin.upload.filerenamepolicy;

import com.jfinal.ext.kit.RandomKit;

import java.io.File;




/**
 * @author BruceZCQ
 * 随机文件名
 * baseSaveDir/xxxxxx.jpg
 */
public class RandomFileRenamePolicy extends FileRenamePolicyWrapper {

	@Override
	public File nameProcess(File f, String name, String ext) {
		String path = f.getParent();
		this.setSaveDirectory(path);
		
		String fileName = RandomKit.randomMD5Str() + ext;
		
		return (new File(path, fileName));
	}
}
