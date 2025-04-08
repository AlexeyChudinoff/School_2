package ru.hogwarts.school_2.servise;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import ru.hogwarts.school_2.model.Faculty;

@Service
public class FacultyService {

  Map<Long, Faculty> facultyMap = new HashMap<>();
  public void initializeFacultyMap() {
    facultyMap.put(1L, new Faculty("Black", "Black"));
    facultyMap.put(2L, new Faculty("Silver", "Silver"));
    facultyMap.put(3L, new Faculty("Gold", "Gold"));
  }

  int idCounter = 0;
  public Faculty addFaculty(Faculty faculty){
    idCounter++;
    faculty.setId((long) idCounter);
    facultyMap.put(faculty.getId(), faculty);
  return faculty;
  }

  public Faculty getFacultyByColor (String color){
    for (Faculty value : facultyMap.values()) {
      if (value.getColor().equals(color)){
        return value;
      }
    }
    return null;
  }

  public Faculty getFacultyById (Long id){
    return facultyMap.get(id);
  }

  public Faculty updateFacultyById(Long id, Faculty faculty){
    faculty.setId(id);
    facultyMap.replace(id, faculty);
    return faculty;
  }


}//class
