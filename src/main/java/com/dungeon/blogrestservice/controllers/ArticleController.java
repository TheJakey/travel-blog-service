package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.ArticleForm;
import com.dungeon.blogrestservice.models.Article;
import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Comment;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.BloggerRepository;
import com.dungeon.blogrestservice.repositories.CommentRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import com.dungeon.blogrestservice.repositories.ArticleRepository;
import com.dungeon.blogrestservice.security.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ArticleController {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    BloggerRepository bloggerRepository;

    @Autowired
    CommentRepository commentRepository;

    // GET - ziskanie udajov o clanku
    @RequestMapping(value = "/articles/full/{id}", method = RequestMethod.GET)
    public ResponseEntity getArticle(@PathVariable long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        Article article;
        ArticleForm articleForm = new ArticleForm();

        if (optionalArticle.isPresent()) {
            article = optionalArticle.get();

            articleForm.setBlogger_id(article.getBloggerId());
            articleForm.setArticle_text(article.getArticleText());
            articleForm.setLikes(article.getNumberOfLikes());
            articleForm.setPublished(article.getPublished());
            articleForm.setTitle(article.getTitle());
            articleForm.setComments(getCommentsFromDB(id));

            return ResponseEntity.status(200).body(articleForm);
        }
        else
            return ResponseEntity.status(400).body("Invalid ID");
    }

    private List<Comment> getCommentsFromDB(long articleId) {
        List<Comment> optionalComments = commentRepository.findAllByArticleId(articleId);

        return optionalComments;
    }

    //TODO pridat tagy k clanku
    //TODO treba osetrit, aby ten isty clanok nevytvoril dvakrat - mozno pridat rovno nejaky "copyright", ze rovnaky obsah je uz vytvoreny
    //TODO treba osterit, aby nemohol vytvorit prazdny clanok, ani "", ani null - title, text (aspon jedno neprazdne slovo) a jeden tag povinny
    //TODO ak JSON neobsahuje jeden z attributov, vytvori clanok - ???
    //TODO special characters treba vyescape-ovat - \t, \"


    // POST - vytvorenie noveho clanku
    @RequestMapping(value = "/articles", method = RequestMethod.POST)
    public ResponseEntity createArticle(@RequestHeader String Token,
                                        @RequestBody ArticleForm articleForm) {

        if (articleForm == null)
            return ResponseEntity.status(400).body("Missing attributes.");

        long id = articleForm.getBlogger_id();

        Optional<Session> optionalSession = sessionRepository.findByBloggerId(id);
        Optional<Blogger> optionalBlogger = bloggerRepository.findById(id);
        Session session;
        Blogger blogger;

        SessionHandler sessionHandler = new SessionHandler(id, Token, optionalSession);

        if(!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if(!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to manipulate with this blogger");

        if (optionalBlogger.isPresent()){
            blogger = optionalBlogger.get();

            String title = articleForm.getTitle();
            String articleText = articleForm.getArticle_text();
            Date published = Calendar.getInstance().getTime();
            int likes = 0;

            Article article;
            article = new Article(id, title, articleText, published, likes);
            articleRepository.save(article);
            return ResponseEntity.status(201).body("");
        } else
             return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");
    }


    // PUT - uprava udajov clanku
    @RequestMapping(value = "/articles/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateArticle(@RequestHeader String Token,
                                        @PathVariable long id,                       // id clanku
                                        @RequestBody ArticleForm articleForm) {

        if (articleForm == null)
            return ResponseEntity.status(400).body("Missing attributes.");

        Optional<Article> articleToUpdate = articleRepository.findById(id);

        if (!articleToUpdate.isPresent()){
            return ResponseEntity.status(400).body("Invalid ID");
        }

        long bloggerId = articleToUpdate.get().getBloggerId();

        Optional<Session> optionalSession = sessionRepository.findByBloggerId(bloggerId);
        Optional<Blogger> optionalBlogger = bloggerRepository.findById(bloggerId);
        Session session;
        Blogger blogger;

        SessionHandler sessionHandler = new SessionHandler(bloggerId, Token, optionalSession);

        if(!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if(!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to manipulate with this blogger");

        if (optionalBlogger.isPresent())
            blogger = optionalBlogger.get();
        else
            return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");

        articleToUpdate.get().setTitle(articleForm.getTitle());
        articleToUpdate.get().setArticleText(articleForm.getArticle_text());
        articleToUpdate.get().setPublished(Calendar.getInstance().getTime());           // posledna zmena
        //articleToUpdate.get().setTag(articleForm.getTag());                   // ked uz budu spravene aj tagy

        articleRepository.save(articleToUpdate.get());
        return ResponseEntity.status(200).body("");
}


    // DELETE - vymazanie clanku
    @RequestMapping(value = "/articles/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteArticle(@RequestHeader String Token,
                                        @PathVariable long id) {      // id clanku !!!

        Optional<Article> articleToDelete;
        articleToDelete = articleRepository.findById(id);

        if (!articleToDelete.isPresent()) {
            return ResponseEntity.status(400).body("Article does not exist.");
        }
        else {
            long blogger_id = articleToDelete.get().getBloggerId();

            Optional<Session> optionalSession = sessionRepository.findByBloggerId(blogger_id);
            Optional<Blogger> optionalBlogger = bloggerRepository.findById(blogger_id);
            Session session;
            Blogger blogger;

            SessionHandler sessionHandler = new SessionHandler(blogger_id, Token, optionalSession);

            if(!sessionHandler.isBloggerLoggedIn())
                return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

            if(!sessionHandler.isTokenMatching())
                return ResponseEntity.status(403).body("You are forbidden to manipulate with this blogger");

            if (optionalBlogger.isPresent())
                blogger = optionalBlogger.get();
            else
                return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");

            articleRepository.delete(articleToDelete.get());
            return ResponseEntity.status(200).body("Article was successfully deleted.");
           }
        }
    }

        // GET - ziskanie dlazdic urcitej kategorie clankov - filter

        // GET - ziskanie obrazku z clanku

        // POST - pridanie komentarov

        // POST - pridanie reakcii na komentar

        // PUT - zmena obrazku v clanku