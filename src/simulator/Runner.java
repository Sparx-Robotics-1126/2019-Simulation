package simulator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Runner {

	private final static String NATIVE_LIBRARY_LOCATION = "data" + File.separator + "nativeLibraries";
	private final static String NATIVE_LIBRARY_ZIP = "data" + File.separator + "nativeLibraries.zip";
	

	public static void main(String[] args) {
		Runner runner = new Runner();
		Process testingControlsRunner = null;
		try {
			runner.setUpApplication();
			testingControlsRunner = Runtime.getRuntime().exec("java -jar resources/TestingControls.jar");
			Thread.sleep(2000);
			if(!testingControlsRunner.isAlive()) {
				File jre = new File("C:/Users/Public/frc2019/jdk/bin/java.exe");
				if(jre.exists())
					testingControlsRunner = Runtime.getRuntime().exec("C:/Users/Public/frc2019/jdk/bin/java.exe -jar resources/TestingControls.jar");
				else {
					System.out.println("Java 11 and FRC Wpilib not Installed!!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		SimMain app = new SimMain();
		app.setControlsProcess(testingControlsRunner);
		app.start();
	}
	
	public static void unzip(File source, String out) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {
			ZipEntry entry = zis.getNextEntry();
			while (entry != null) {
				File file = new File(out, entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
						byte[] buffer = new byte[Math.toIntExact(entry.getSize())];
						int location;
						while ((location = zis.read(buffer)) != -1) {
							bos.write(buffer, 0, location);
						}
					}
				}
				entry = zis.getNextEntry();
			}
		}
	}
	
	public void setUpApplication() throws IOException {
		File fileDir = new File(NATIVE_LIBRARY_LOCATION);
		if(!fileDir.exists()) {
			File data = fileDir.getParentFile();
			File resources = new File(data.getAbsolutePath().substring(0, data.getAbsolutePath().lastIndexOf(File.separator)) + File.separator + "resources");
			if(!data.exists()) {
				data.mkdirs();
				resources.mkdirs();
				Files.copy(this.getClass().getResourceAsStream("/data/properties.xml"), Paths.get(data.getAbsolutePath(), "properties.xml"));
				Files.copy(this.getClass().getResourceAsStream("/data/tablesProperties.xml"), Paths.get(data.getAbsolutePath(), "tablesProperties.xml"));
				Files.copy(this.getClass().getResourceAsStream("/data/TestingControls.jar"), Paths.get(resources.getAbsolutePath(), "TestingControls.jar"));
			}
			try {
				fileDir.mkdirs();
				File file = new File(NATIVE_LIBRARY_ZIP);
				Files.copy(this.getClass().getResourceAsStream("/data/nativeLibraries.zip"), Paths.get(file.getParentFile().getAbsolutePath(), "nativeLibraries.zip"));
				unzip(file, file.getParent());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		try {
			System.setProperty("java.library.path", fileDir.getAbsolutePath());
			Field sysPath = ClassLoader.class.getDeclaredField("sys_paths");
			sysPath.setAccessible(true);
			sysPath.set(null, null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
