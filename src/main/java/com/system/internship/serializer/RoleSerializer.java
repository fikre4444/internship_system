package com.system.internship.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.system.internship.domain.Role;

public class RoleSerializer extends JsonSerializer<Role> {
  @Override
  public void serialize(Role role, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("role", role.getName().name());
    jsonGenerator.writeStringField("name", role.getName().getName());
    jsonGenerator.writeEndObject();
  }
}
