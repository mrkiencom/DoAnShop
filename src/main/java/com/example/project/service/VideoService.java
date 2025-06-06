package com.example.project.service;

import com.example.project.model.Courses;
import com.example.project.model.Videos;
import com.example.project.repo.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VideoService {
    @Autowired
    VideoRepo videoRepo;

    public Videos findVideoById(Integer videoId) {
        Optional<Videos> optionalVideos = videoRepo.findById(videoId);
        return optionalVideos.orElse(null);
    }

}
