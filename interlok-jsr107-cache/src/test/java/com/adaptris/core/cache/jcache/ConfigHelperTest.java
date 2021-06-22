package com.adaptris.core.cache.jcache;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.adaptris.core.CoreException;
import com.adaptris.core.stubs.TempFileUtils;

public class ConfigHelperTest extends ConfigHelper {

  @Test
  public void testFromFile() throws Exception {
    File f1 = TempFileUtils.createTrackedDir(this);
    assertNotNull(Builder.FromFile.build("file://localhost/" + f1.getCanonicalPath()));
    // TempFileUtils.createTrackedFile should delete the file.
    File f2 = TempFileUtils.createTrackedFile(this);
    try {
      Builder.FromFile.build("file:///localhost/" + f2.getCanonicalPath());
      fail();
    } catch (FileNotFoundException expected) {

    }
  }

  @Test
  public void testFromURI() throws Exception {
    assertNotNull(Builder.FromURI.build("http://localhost:80/1/2/3/4"));
  }

  @Test
  public void testViaURL() throws Exception {
    File f1 = TempFileUtils.createTrackedDir(this);
    assertNotNull(Builder.ViaURL.build("file://localhost/" + f1.getCanonicalPath()));
    assertNotNull(Builder.ViaURL.build("./build.gradle"));
    assertNotNull(Builder.ViaURL.build("http://localhost:80/1/2/3/4"));
  }


  @Test
  public void testAsURI() throws Exception {
    assertThrows(CoreException.class, () -> asURI(null));

    File f1 = TempFileUtils.createTrackedDir(this);
    assertNotNull(asURI("file://localhost/" + f1.getCanonicalPath()));
  }

}
