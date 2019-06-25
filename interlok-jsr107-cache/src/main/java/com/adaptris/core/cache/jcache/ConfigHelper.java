package com.adaptris.core.cache.jcache;

import static com.adaptris.core.fs.FsHelper.toFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.lang3.BooleanUtils;
import com.adaptris.core.CoreException;
import com.adaptris.util.URLString;

abstract class ConfigHelper {

  protected enum Builder {
    FromFile {
      @Override
      URI build(String path) throws IOException, URISyntaxException {
        File f = toFile(path);
        if (f.exists()) {
          return f.toURI();
        }
        throw new FileNotFoundException();
      }
    },
    FromURI {
      @Override
      URI build(String path) throws IOException, URISyntaxException {
        return new URI(path);
      }
    },
    ViaURL {
      @Override
      URI build(String path) throws IOException, URISyntaxException {
        if (probablyLocalFile(path)) {
          return FromFile.build(path);
        }
        return new URL(path).toURI();
      }

    };

    abstract URI build(String path) throws IOException, URISyntaxException;

  }

  private static boolean probablyLocalFile(String loc) throws IOException {
    URLString url = new URLString(loc);
    return BooleanUtils
        .or(new boolean[] {
        url.getProtocol() == null, "file".equals(url.getProtocol())
    });
  }

  protected static URI asURI(final String path) throws CoreException {
    for (Builder c : Builder.values()) {
      try {
        return c.build(path);
      } catch (Exception e) {

      }
    }
    throw new CoreException("Could not parse into a URI [" + path + "]");
  }

}
