package com.dungeon.blogrestservice.forms;

public class FollowerForm {
        private  long id;
        private  String username;

        protected FollowerForm() {};

        public FollowerForm(long id, String username){
            this.id = id;
            this.username = username;
        };

        public String getUsername() {
            return this.username;
        }

    public long getId() {
        return this.id;
    }
}
