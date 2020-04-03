package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.RegisterForm;
import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.BloggerRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import com.dungeon.blogrestservice.security.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class BloggerController {
    private final static String MAPPING_VALUE = "/bloggers";
    private final static String CONTENT_TYPE_JPEG = "image/jpeg";

    @Autowired
    BloggerRepository repository;

    @Autowired
    SessionRepository sessionRepository;

    @RequestMapping(value = MAPPING_VALUE + "/{id}", method = RequestMethod.GET)
    public ResponseEntity getBlogger(@PathVariable long id) {
        Optional<Blogger> blogger;

        blogger = repository.findById(id);

        if (!blogger.isPresent())
            return ResponseEntity.status(400).body("Invalid ID - User with such ID doesn't exists");

        return ResponseEntity.status(200).header("Token", "totojetoken").body(blogger.get());
    }

    @RequestMapping(value = MAPPING_VALUE, method = RequestMethod.POST)
    public ResponseEntity createBlogger(@RequestBody RegisterForm registerForm) {
        Blogger new_blogger;
        String username = registerForm.getUsername();
        String email = registerForm.getEmail();
        String password = registerForm.getPassword();

        // any nulls?
        if (username == null || email == null || password == null)
            return ResponseEntity.status(400).body("Missing mandatory field");

        // verify if all mandatory data are provided
        if (username.isEmpty() || email.isEmpty() || password.isEmpty())
            return ResponseEntity.status(400).body("Missing mandatory field");

        // verify if username already exists
        if (repository.findBloggerByUsername(username) != null)
            return ResponseEntity.status(400).body("Username already exists");

        new_blogger = new Blogger(username, registerForm.getAbout_me(), email, password);

        repository.save(new_blogger);

        return ResponseEntity.status(201).body("");
    }

    @RequestMapping(value = MAPPING_VALUE + "/{id}", method = RequestMethod.PUT)
    public ResponseEntity replaceBlogger(
            @PathVariable long id,
            @RequestHeader(value = "token") String requestToken,
            @RequestBody RegisterForm registerForm
    ) {
        String new_username = registerForm.getUsername();
        String new_about_me = registerForm.getAbout_me();
        String new_email = registerForm.getEmail();
        String new_password = registerForm.getPassword();

        Optional<Session> optionalSession = sessionRepository.findByBloggerId(id);
        Optional<Blogger> optionalBlogger = repository.findById(id);
        Session session;
        Blogger blogger;
        SessionHandler sessionHandler = new SessionHandler(id, requestToken, optionalSession);


        if(!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if(!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to manipulate with this blogger");

        if (optionalBlogger.isPresent())
            blogger = optionalBlogger.get();
        else
            return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");

        // be nice to them, change only what's changed ONLY till PATCH method is implemented
        if (new_username != null) {
            if (blogger.getUsername().compareTo(new_username) == 0)
                blogger.setUsername(new_username);
            // verify if new_username already exists
            else if (repository.findBloggerByUsername(new_username) != null)
                return ResponseEntity.status(400).body("New username already exists");
            else
                blogger.setUsername(new_username);
        }
        if (new_email != null) {
            blogger.setEmail(new_email);
        }
        if (new_about_me != null) {
            blogger.setAboutMe(new_about_me);
        }
        if (new_password != null) {
            blogger.setPassword(new_password);
        }

        repository.save(blogger);

        return ResponseEntity.status(200).body("");
    }

    @RequestMapping(value = MAPPING_VALUE + "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteBlogger(@PathVariable long id, @RequestHeader(value = "token") String requestToken) {
        Optional<Blogger> optionalBlogger = repository.findById(id);
        Optional<Session> optionalSession = sessionRepository.findByBloggerId(id);
        Blogger blogger;
        SessionHandler sessionHandler = new SessionHandler(id, requestToken, optionalSession);

        if(!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if(!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to delete this user");

        if (optionalBlogger.isPresent())
            blogger = optionalBlogger.get();
        else
            return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");

        sessionRepository.delete(sessionHandler.getSession());
        repository.delete(blogger);

        return ResponseEntity.status(200).body("");
    }

    @RequestMapping(value = MAPPING_VALUE + "/photos", method = RequestMethod.POST)
    public ResponseEntity uploadNewPhoto(
            @RequestParam(value = "bloggerId") long bloggerId,
            @RequestParam(value = "type") String type,
            @RequestHeader(value = "token") String requestToken,                // TODO: FIX token to Token with VIKI !!
            @RequestHeader(value = "Content-Type") String contentType,
            @RequestBody byte[] photo
    ) {
        Optional<Blogger> optionalBlogger = repository.findById(bloggerId);
        Optional<Session> optionalSession = sessionRepository.findByBloggerId(bloggerId);
        Blogger blogger;
        SessionHandler sessionHandler = new SessionHandler(bloggerId, requestToken, optionalSession);

        if(!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if(!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to change this photo");

        if (optionalBlogger.isPresent())
            blogger = optionalBlogger.get();
        else
            return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");

//        // accept only jpeg\
//        if (contentType.compareTo(CONTENT_TYPE_JPEG) != 0)
//            return ResponseEntity.status(400).body("Only JPEG is accepted");

        if (type.compareTo("profile") == 0)
            blogger.setProfilePhoto(photo);
        else if (type.compareTo("cover") == 0)
            blogger.setCoverPhoto(photo);
        else
            return ResponseEntity.status(400).body("Invalid Type (this means parameter 'type' - allowed values 'profile' and 'cover')");

        repository.save(blogger);

        return ResponseEntity.status(201).body("");
    }

    @RequestMapping(value = MAPPING_VALUE + "/photos", method = RequestMethod.PUT)
    public ResponseEntity updatePhoto(
            @RequestParam(value = "bloggerId") long bloggerId,
            @RequestParam(value = "type") String type,
            @RequestHeader(value = "token") String requestToken,                // TODO: FIX token to Token with VIKI !!
            @RequestHeader(value = "Content-Type") String contentType,
            @RequestBody byte[] photo
    ) {
        Optional<Blogger> optionalBlogger = repository.findById(bloggerId);
        Optional<Session> optionalSession = sessionRepository.findByBloggerId(bloggerId);
        Blogger blogger;
        SessionHandler sessionHandler = new SessionHandler(bloggerId, requestToken, optionalSession);

        if(!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if(!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to change this photo");

        if (optionalBlogger.isPresent())
            blogger = optionalBlogger.get();
        else
            return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");

//        // accept only jpeg\
//        if (contentType.compareTo(CONTENT_TYPE_JPEG) != 0)
//            return ResponseEntity.status(400).body("Only JPEG is accepted");

        if (type.compareTo("profile") == 0)
            blogger.setProfilePhoto(photo);
        else if (type.compareTo("cover") == 0)
            blogger.setCoverPhoto(photo);
        else
            return ResponseEntity.status(400).body("Invalid Type (this means parameter 'type' - allowed values 'profile' and 'cover')");

        repository.save(blogger);

        return ResponseEntity.status(200).body("");
    }

    @RequestMapping(value = MAPPING_VALUE + "/photos", method = RequestMethod.GET)
    public ResponseEntity getBloggerPhoto(
            @RequestParam(value = "bloggerId") long bloggerId,
            @RequestParam(value = "type") String type
    ) {
        Optional<Blogger> blogger;
        HttpHeaders headers = new HttpHeaders();
        byte[] photo = null;

        blogger = repository.findById(bloggerId);

        // verify if is NOT blogger exists
        if (!blogger.isPresent())
            return ResponseEntity.status(400).body("Non-existing blogger for provided id");

        if (type.compareTo("profile") == 0)
            photo = blogger.get().getProfilePhoto();
        else if (type.compareTo("cover") == 0)
            photo = blogger.get().getCoverPhoto();
        else
            return ResponseEntity.status(400).body("Invalid Type (this means parameter 'type' - allowed values 'profile' and 'cover')");

        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        return ResponseEntity.status(200).contentType(MediaType.parseMediaType("image/jpeg")).cacheControl(CacheControl.noCache()).body(photo);
    }

    @RequestMapping(value = MAPPING_VALUE + "/photos", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePhoto(
            @RequestParam(value = "bloggerId") long bloggerId,
            @RequestParam(value = "type") String type,
            @RequestHeader(value = "token") String requestToken                 // TODO: FIX token to Token with VIKI !!
    ) {
        Optional<Blogger> optionalBlogger = repository.findById(bloggerId);
        Optional<Session> optionalSession = sessionRepository.findByBloggerId(bloggerId);
        Blogger blogger;
        SessionHandler sessionHandler = new SessionHandler(bloggerId, requestToken, optionalSession);

        if(!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if(!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to delete this photo");

        if (optionalBlogger.isPresent())
            blogger = optionalBlogger.get();
        else
            return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");

        if (type.compareTo("profile") == 0)
            blogger.setProfilePhoto(null);
        else if (type.compareTo("cover") == 0)
            blogger.setCoverPhoto(null);
        else
            return ResponseEntity.status(400).body("Invalid Type (this means parameter 'type' - allowed values 'profile' and 'cover')");

        repository.save(blogger);

        return ResponseEntity.status(200).body("");
    }
}
