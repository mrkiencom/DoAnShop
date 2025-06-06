package com.example.project.service;

import com.example.project.model.Courses;
import com.example.project.model.Videos;
import com.example.project.repo.CourseRepo;
import com.example.project.repo.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LecturerService {

    @Autowired
    CourseRepo courseRepo;
    @Autowired
    VideoRepo videoRepo;

    public Integer createCourses(Courses course) {
        Date currentDate = new Date();
        if(course.getPrice() == 0){
            course.setIs_paid(false);
        }
        else {
            course.setIs_paid(true);
        }
        course.setDate(currentDate);
        course.setStatus("Waiting");
        Courses savedCourse = courseRepo.save(course);
        return savedCourse.getCourseId();
    }

    public void uploadVideo(Videos video, Courses Course) {
        Date currentDate = new Date();
        video.setUploadedAt(currentDate);
        video.setCourse(Course);
        videoRepo.save(video);
    }

    public Courses getCourseById(Integer courseId) {
        Optional<Courses> optionalCourse = courseRepo.findById(courseId);
        return optionalCourse.orElse(null);
    }

    public List<Courses> getCoursesByLecturer(Integer lecturerId) {
        return courseRepo.findByLecturerId(lecturerId);
    }

    public void saveCourse(Courses course) {
        courseRepo.save(course);
    }

    public void deleteCourseById(Integer courseId) {
        courseRepo.deleteById(courseId);
    }

    public Videos getVideoById(Integer videoId) {
        Optional<Videos> optionalVideo = videoRepo.findById(videoId);
        return optionalVideo.orElse(null);
    }

    public void saveVideo(Videos video) {
        videoRepo.save(video);
    }

    public void deleteVideoById(Integer videoId) {
        videoRepo.deleteById(videoId);
    }
}
