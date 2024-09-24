package com.system.internship.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.system.internship.domain.Role;
import com.system.internship.enums.DepartmentEnum;

public class DepartmentSerializer extends JsonSerializer<DepartmentEnum> {
  @Override
  public void serialize(DepartmentEnum departmentEnum, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("department", departmentEnum.name());
    jsonGenerator.writeStringField("name", departmentEnum.getDepartmentName());
    jsonGenerator.writeEndObject();
  }
}
