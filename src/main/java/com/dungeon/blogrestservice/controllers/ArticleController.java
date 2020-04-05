package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.ArticleForm;
import com.dungeon.blogrestservice.models.*;
import com.dungeon.blogrestservice.repositories.*;
import com.dungeon.blogrestservice.security.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ArticleController {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    BloggerRepository bloggerRepository;

    @Autowired
    PagingAndSortingRepository pagingAndSortingRepository;
    
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ArticlePhotoRepository articlePhotoRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ArticleTagRepository articleTagRepository;


    // GET - ziskanie udajov o clanku
    @RequestMapping(value = "/articles/full/{id}", method = RequestMethod.GET)
    public ResponseEntity getArticle(@PathVariable long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        Article article;
        ArticleForm articleForm = new ArticleForm();
        List<ArticleTag> articleTags;
        List<Tag> tags = new LinkedList<>();

        if (optionalArticle.isPresent()) {
            article = optionalArticle.get();

            articleForm.setBlogger_id(article.getBloggerId());
            articleForm.setArticle_text(article.getArticleText());
            articleForm.setLikes(article.getLikes());
            articleForm.setPublished(article.getPublished());
            articleForm.setTitle(article.getTitle());
            articleForm.setComments(getCommentsFromDB(id));
            articleForm.setNumberOfPhotosInGallery(articlePhotoRepository.findAllByArticleId(id).size());

            // get tags from db
            articleTags = articleTagRepository.findAllByArticleId(id);
            for (ArticleTag articleTag : articleTags) {
                tags.add(tagRepository.findById(articleTag.getTagId()).get());
            }
            articleForm.setTags(tags);

            return ResponseEntity.status(200).body(articleForm);
        }
        else
            return ResponseEntity.status(400).body("Invalid ID");
    }

    private List<Comment> getCommentsFromDB(long articleId) {
        List<Comment> optionalComments = commentRepository.findAllByArticleId(articleId);

        return optionalComments;
    }

  
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
        List<ArticleTag> articleTags = new LinkedList<ArticleTag>();
        long articalId;

        SessionHandler sessionHandler = new SessionHandler(id, Token, optionalSession);

        if (!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if (!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to manipulate with this blogger");

        if (optionalBlogger.isPresent()) {
            blogger = optionalBlogger.get();

            String title = articleForm.getTitle();
            String articleText = articleForm.getArticle_text();
            Date published = Calendar.getInstance().getTime();
            int likes = 0;

            Article article;
            article = new Article(id, title, articleText, published, likes);

            Article just_created_article = articleRepository.save(article);
            articalId = just_created_article.getId();

            // add tags
            Optional<Tag> optionalTag;
            Tag tag;
            Tag new_tag;
            for (String tagName : articleForm.getSelected_tags()) {
                if (tagName.isEmpty())
                    continue;

                optionalTag = tagRepository.findByTag(tagName);

                if (optionalTag.isPresent()) {
                    articleTags.add(new ArticleTag(articalId, optionalTag.get().getId()));
                }
                else {
                    tag = new Tag(tagName);
                    new_tag = tagRepository.save(tag);

                    articleTags.add(new ArticleTag(articalId, new_tag.getId()));
                }
            }

            if (articleTags.size() > 0)
                articleTagRepository.saveAll(articleTags);

            return ResponseEntity.status(201).body(articalId);
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

        if (!articleToUpdate.isPresent()) {
            return ResponseEntity.status(400).body("Invalid ID");
        }

        long bloggerId = articleToUpdate.get().getBloggerId();

        Optional<Session> optionalSession = sessionRepository.findByBloggerId(bloggerId);
        Optional<Blogger> optionalBlogger = bloggerRepository.findById(bloggerId);
        Session session;
        Blogger blogger;

        SessionHandler sessionHandler = new SessionHandler(bloggerId, Token, optionalSession);

        if (!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if (!sessionHandler.isTokenMatching())
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
        } else {
            long blogger_id = articleToDelete.get().getBloggerId();

            Optional<Session> optionalSession = sessionRepository.findByBloggerId(blogger_id);
            Optional<Blogger> optionalBlogger = bloggerRepository.findById(blogger_id);
            Session session;
            Blogger blogger;

            SessionHandler sessionHandler = new SessionHandler(blogger_id, Token, optionalSession);

            if (!sessionHandler.isBloggerLoggedIn())
                return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

            if (!sessionHandler.isTokenMatching())
                return ResponseEntity.status(403).body("You are forbidden to manipulate with this blogger");

            if (optionalBlogger.isPresent())
                blogger = optionalBlogger.get();
            else
                return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");

            articlePhotoRepository.deleteAllByArticleId(id);
            commentRepository.deleteAllByArticleId(id);
            articleRepository.delete(articleToDelete.get());

            return ResponseEntity.status(200).body("Article was successfully deleted.");
        }
    }


    // GET - ziskanie dlazdic urcitej kategorie clankov - filter

    @RequestMapping(value = "/articles/tile", method = RequestMethod.GET)
    public ResponseEntity getArticleTile(@RequestParam(required = false, value = "id") Long id,
                                         @RequestParam(required = false, value = "type") String categoryType,
                                         @RequestParam(required = false, value = "first") Integer first_article,
                                         @RequestParam(required = false, value = "limit") Integer limit,
                                         @RequestParam(required = false, value = "order") Character order) {

        Sort sort = null;
        if (id == null && categoryType == null && first_article == null && limit == null && order == null){
            sort = Sort.by("published").descending();
            first_article = 0;
            limit = 10;
        }
        else {

            if (id != null)
                return ResponseEntity.status(200).body(articleRepository.findById(id));

            // Articles can be sorted by the categoryType given in request: popular, date , title
            if (categoryType != null) {

                if (categoryType.compareTo("popular") == 0)
                    sort = Sort.by("likes");
                else if ((categoryType.compareTo("date") == 0))
                    sort = Sort.by("published");
                else if (categoryType.compareTo("articletitle") == 0)
                    sort = Sort.by("title");
                else
                    return ResponseEntity.status(400).body("");
            } else
                sort = Sort.by("published");

            // Ordered by ASC / DESC
            if (order != null) {
                if (order == 'a')
                    sort = sort.ascending();
                else {
                    if (order == 'd')
                        sort = sort.descending();
                    else
                        return ResponseEntity.status(400).body("");
                }
            } else
                sort = sort.descending();

            // Pagination from 'first_article' showing #'limit' articles
            if (first_article == null)
                first_article = 0;

            if (limit == null)
                limit = 10;

        }
        Pageable paging = PageRequest.of(first_article, limit, sort);
        Iterable<Article> articleIterator = articleRepository.findAll(paging);

        if (articleIterator == null)
            return ResponseEntity.status(404).body("");
        else
            return ResponseEntity.status(200).body(articleIterator);
    }

    // GET - ziskanie obrazku z clanku


    // PUT - zmena obrazku v clanku

}