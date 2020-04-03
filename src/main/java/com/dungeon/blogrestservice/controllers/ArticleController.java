package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.ArticleForm;
import com.dungeon.blogrestservice.models.Article;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import com.dungeon.blogrestservice.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@RestController
public class ArticleController {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    SessionRepository sessionRepository;

    // GET - ziskanie udajov o clanku
    @RequestMapping(value = "/articles/full/{id}", method = RequestMethod.GET)
    public ResponseEntity getArticle(@PathVariable long id) {

        Optional<Article> article;
        article = articleRepository.findById(id);

        if (article != null)
            return ResponseEntity.status(200).body(article);
        else
            return ResponseEntity.status(400).body("Invalid ID");
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

        Optional<Session> session;
        long bloggerId = articleForm.getBlogger_id();
        session = sessionRepository.findByBloggerId(bloggerId);

        if (!session.isPresent()) {
            return ResponseEntity.status(400).body("Invalid ID");
        }
        else {
            String sessionToken = session.get().getToken();
            if (Token.compareTo(sessionToken) == 0) {

                String title = articleForm.getTitle();
                String articleText = articleForm.getArticle_text();
                Date published = Calendar.getInstance().getTime();
                int likes = 0;

                Article article;
                article = new Article(bloggerId, title, articleText, published, likes);
                articleRepository.save(article);
                return ResponseEntity.status(201).body("");
            } else
                return ResponseEntity.status(401).body("");
        }
    }

    // PUT - uprava udajov clanku
    @RequestMapping(value = "/articles/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateArticle(@RequestHeader String Token,
                                        @PathVariable long id,  // id clanku
                                        @RequestBody ArticleForm articleForm) {

        if (articleForm == null)
            return ResponseEntity.status(400).body("Missing attributes.");

        Optional<Article> articleToUpdate = articleRepository.findById(id);
        if (!articleToUpdate.isPresent()){
            return ResponseEntity.status(400).body("Invalid ID");
        }
        // TODO pridat verifikaciu
        Optional<Session> session;
        long bloggerId = articleToUpdate.get().getBloggerId();
        session = sessionRepository.findByBloggerId(bloggerId);

        if (!session.isPresent()) {
            return ResponseEntity.status(400).body("Invalid ID");
        }
        else {
            String sessionToken = session.get().getToken();
            if (Token.compareTo(sessionToken) == 0) {

                articleToUpdate.get().setTitle(articleForm.getTitle());
                articleToUpdate.get().setArticleText(articleForm.getArticle_text());
                articleToUpdate.get().setPublished(Calendar.getInstance().getTime());           // posledna zmena
                //articleToUpdate.get().setTag(articleForm.getTag());                   // ked uz budu spravene aj tagy

                articleRepository.save(articleToUpdate.get());
                return ResponseEntity.status(200).body("");
            } else
                return ResponseEntity.status(401).body("");
        }
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
            Optional<Session> session;
            long blogger_id = articleToDelete.get().getBloggerId();
            session = sessionRepository.findByBloggerId(blogger_id);

            //TODO treba pridat moznost Forbidden to delete this article

             if (!session.isPresent()) {
                 return ResponseEntity.status(400).body("");
             }
             else {
                String sessionToken = session.get().getToken();
                if (Token.compareTo(sessionToken) == 0) {

                    articleRepository.delete(articleToDelete.get());
                    return ResponseEntity.status(200).body("Article was successfully deleted.");
                } else
                    return ResponseEntity.status(401).body("");
            }
        }
    }

        // GET - ziskanie dlazdic urcitej kategorie clankov - filter

        // GET - ziskanie obrazku z clanku

        // POST - pridanie komentarov

        // POST - pridanie reakcii na komentar

        // PUT - zmena obrazku v clanku

}