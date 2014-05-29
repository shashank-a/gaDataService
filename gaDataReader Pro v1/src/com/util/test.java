package com.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;


import com.google.appengine.api.files.*;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * A main method to show how to use the GCS client locally.
 *
 */
public class test {

  /**
   * Use this to make the library run locally as opposed to in a deployed servlet.
   *
   * Writes a map to GCS and then reads it back printing the result to standard out.
   * Then does the same for a byte array.
   * (You may wish to suppress stderr as there is a lot of noise)
   */
  
}
