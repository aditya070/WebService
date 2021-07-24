package com.example.uploadingfiles.model;
import org.springframework.core.io.Resource;

import java.nio.file.Path;

public interface ModelService {

	void generatePressureProfile(Path loc);

	Resource getPressurePath(String filename);

}