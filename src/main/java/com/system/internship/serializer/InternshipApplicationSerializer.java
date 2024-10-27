package com.system.internship.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.system.internship.domain.InternshipApplication;

public class InternshipApplicationSerializer extends JsonSerializer<InternshipApplication> {

  @Override
  public void serialize(InternshipApplication internshipApplication, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider)
      throws IOException {
    internshipApplication.getStudent().setInternshipApplications(null);
    internshipApplication.getInternshipOpportunity().setInternshipApplications(null);
    jsonGenerator.writeStartObject();
    jsonGenerator.writeObjectField("student", internshipApplication.getStudent());
    jsonGenerator.writeObjectField("internshipOpportunity", internshipApplication.getInternshipOpportunity());
    jsonGenerator.writeObjectField("priority", internshipApplication.getPriority());
    jsonGenerator.writeEndObject();
  }

}
