package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.FollowerForm;
import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Follower;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.BloggerRepository;
import com.dungeon.blogrestservice.repositories.FollowerRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import com.dungeon.blogrestservice.security.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
public class FollowerController {

    @Autowired
    FollowerRepository followerRepository;

    @Autowired
    BloggerRepository bloggerRepository;

    @Autowired
    SessionRepository sessionRepository;

    @RequestMapping(value = "/bloggers/{id}/followers", method = RequestMethod.GET)
    public ResponseEntity getBloggersFollows(@PathVariable long id) {        // koho sleduje blogger
        Optional<Follower> follower;
        Optional<Blogger> blogger;

        if (id == 0)
            return ResponseEntity.status(400).body("");

        List<Follower> followersOfBlogger = followerRepository.findAllByBloggerId(id);

        if (followersOfBlogger == null)
            return ResponseEntity.status(400).body("Invalid ID - User with such ID doesn't exist");

        List<FollowerForm> folFormList = addFollowerToList(followersOfBlogger);
        return ResponseEntity.status(200).body(folFormList);
    }

    public List<FollowerForm> addFollowerToList(List<Follower> followersOfBlogger){
        FollowerForm followerForm;
        List<FollowerForm> followerList = new LinkedList<FollowerForm>();

        long followerId = 0;
        Optional<Blogger> blogger;
        for(Follower follower : followersOfBlogger) {
            followerId = follower.getFollowerId();
            blogger = bloggerRepository.findById(followerId);
            followerForm = new FollowerForm(followerId, blogger.get().getUsername());
            followerList.add(followerForm);
        }
        return followerList;
    }

    public List<FollowerForm> addFanToList(List<Follower> followersOfBlogger){
        FollowerForm followerForm;
        List<FollowerForm> followerList = new LinkedList<FollowerForm>();

        long followerId = 0;
        Optional<Blogger> blogger;
        for(Follower follower : followersOfBlogger) {
            followerId = follower.getBloggerId();
            blogger = bloggerRepository.findById(followerId);
            followerForm = new FollowerForm(followerId, blogger.get().getUsername());
            followerList.add(followerForm);
        }
        return followerList;
    }

    @RequestMapping(value = "/bloggers/followers/{id}", method = RequestMethod.GET)
    public ResponseEntity getBloggersFanclub(@PathVariable long id) {              // kto sleduje bloggera - fanclub
        Optional<Follower> follower;
        Optional<Blogger> blogger;

        if (id == 0)
            return ResponseEntity.status(400).body("");

        List<Follower> bloggersFanclub = followerRepository.findAllByFollowerId(id);

        if (bloggersFanclub == null)
            return ResponseEntity.status(400).body("Invalid ID - User with such ID doesn't exist");

        List<FollowerForm> fanclubFormList = addFanToList(bloggersFanclub);
        return ResponseEntity.status(200).body(fanclubFormList);
    }


    @RequestMapping(value = "/bloggers/{id_blogger}/followers/{id_follower}", method = RequestMethod.POST)
    public ResponseEntity followBlogger(@PathVariable long id_blogger,
                                        @PathVariable long id_follower,
                                        @RequestHeader(value = "token") String requestToken){

        // Authentification of blogger who want to follow another blogger
        Optional<Session> optionalSession = sessionRepository.findByBloggerId(id_blogger);
        Optional<Blogger> optionalBlogger = bloggerRepository.findById(id_blogger);
        Session session;
        Blogger blogger;

        SessionHandler sessionHandler = new SessionHandler(id_blogger, requestToken, optionalSession);

        if(!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if(!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to manipulate with this blogger");

        if (optionalBlogger.isPresent())
            blogger = optionalBlogger.get();
        else
            return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");


        if (id_blogger == id_follower)
            return ResponseEntity.status(400).body("");

        if (id_blogger == 0 || id_follower == 0)
            return ResponseEntity.status(400).body("");

        Optional<Blogger> followingBlogger = bloggerRepository.findById(id_follower);

        if (!optionalBlogger.isPresent() || !followingBlogger.isPresent())
            return ResponseEntity.status(400).body("Invalid ID - user with such ID doesn't exist");

        List<Follower> bloggersFanclub = followerRepository.findAllByBloggerId(id_blogger);

        if (bloggersFanclub.contains(id_follower))
                return ResponseEntity.status(400).body("You are already following this blogger");

        Follower newFollower = new Follower(id_blogger, id_follower);
        followerRepository.save(newFollower);

        return ResponseEntity.status(201).body("");
    }

    @RequestMapping(value = "/bloggers/{id}/followers/{follower_id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteFollower(@PathVariable long id,
                                         @PathVariable long follower_id,
                                         @RequestHeader(value = "token") String requestToken) {

        // Authentification
        Optional<Blogger> optionalBlogger = bloggerRepository.findById(id);
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

        boolean found = false;
        long rowId;
        List<Follower> followers = followerRepository.findAllByBloggerId(id);
        for (Follower follower : followers){
            if (follower.getFollowerId() == follower_id)
                found = true;
                followerRepository.delete(follower);
        }
        if (found == true)
            return ResponseEntity.status(200).body("");
        else
            return ResponseEntity.status(400).body("You do not follow that blogger.");
    }

}
