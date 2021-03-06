/*
 * Copyright 2016 joses.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.josesousa9000.easywarp.warps;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author joses
 */
class Serializer {

  private final JsonFactory f;
  private final ObjectMapper mapper;
  private final JavaType type;

  public Serializer() {
    this.f = new CBORFactory();
    this.mapper = new ObjectMapper(f);
    type = mapper.getTypeFactory().constructMapType(Map.class, String.class, PlayerWarps.class);
    mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE).withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
  }

  public void saveToFile(Map<String, PlayerWarps> warps, File file) {
    try {
      Files.write(mapper.writeValueAsBytes(warps), file);
    } catch (IOException ex) {
      Logger.getGlobal().log(Level.WARNING, "Oops! ", ex);
    }
  }

  public Map<String, PlayerWarps> loadFromFile(File file) throws IOException {
    try {
      if (!file.exists() || file.length() == 0) {
        return new HashMap<>();
      }
      return mapper.readValue(FileUtils.readFileToByteArray(file), type);
    } catch (IOException ex) {
      Logger.getGlobal().log(Level.WARNING, "Oops! ", ex);
      return new HashMap<>();
    }
  }
}
