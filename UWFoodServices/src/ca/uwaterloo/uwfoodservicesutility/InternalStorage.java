package ca.uwaterloo.uwfoodservicesutility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

public class InternalStorage{
	 
	   private InternalStorage() {
		   
	   }
	 
	   public static void writeObject(Context context, String key, Object object) throws IOException {
	      FileOutputStream fileOutputStream = context.openFileOutput(key, Context.MODE_PRIVATE);
	      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
	      objectOutputStream.writeObject(object);
	      objectOutputStream.close();
	      fileOutputStream.close();
	   }
	 
	   public static Object readObject(Context context, String key) throws IOException,
	         ClassNotFoundException {
	      FileInputStream fileInputStream = context.openFileInput(key);
	      ObjectInputStream objectInputSteam = new ObjectInputStream(fileInputStream);
	      Object object = objectInputSteam.readObject();
	      return object;
	   }
	}