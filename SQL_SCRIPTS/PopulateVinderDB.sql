USE VinderDB;

drop procedure if exists populate_users_vinder_db;

delimiter #
create procedure populate_users_vinder_db()
begin

declare v_max int unsigned default 1000;
declare v_counter int unsigned default 1;
DECLARE str VARCHAR(255);
DECLARE em VARCHAR(255);

  start transaction;
  while v_counter < v_max do
	SET str = 'Tommy';
    SET em = '';
    SET str = CONCAT(str,v_counter,'');
	SET em = CONCAT(em, str, '@usc.edu');
    INSERT INTO Users (Date, Name, UserName, Email, Password, Bio, Avatar) VALUES ('Sat Apr 18 14:03:10 PDT 2020', str, str, em, 'traveler', 'Palmam qui meruit ferat!', 'https://usctrojans.com/images/2018/2/8/usc_day_in_troy_mcgillen_012917_3907.jpg');
    set v_counter=v_counter+1;
  end while;
  commit;
end #

delimiter ;

call populate_users_vinder_db();

drop procedure if exists populate_posts_vinder_db;

delimiter #
create procedure populate_posts_vinder_db()
begin

declare p_max int unsigned default 1000;
declare p_counter int unsigned default 1;
DECLARE str VARCHAR(255);

  start transaction;
  while p_counter < p_max do
	SET str = 'Tommy';
    SET str = CONCAT(str,p_counter,'');
    INSERT INTO Posts (Date, AuthorName, AuthorID, Content) VALUES ('Sat Apr 18 14:03:10 PDT 2020', str, p_counter, 'Post#1 Content. Palmam qui meruit ferat!');
    INSERT INTO Posts (Date, AuthorName, AuthorID, Content) VALUES ('Sat Apr 18 14:03:10 PDT 2020', str, p_counter, 'Post#2 Content. Palmam qui meruit ferat!');
    INSERT INTO Posts (Date, AuthorName, AuthorID, Content) VALUES ('Sat Apr 18 14:03:10 PDT 2020', str, p_counter, 'Post#3 Content. Palmam qui meruit ferat!');
    set p_counter=p_counter+1;
  end while;
  commit;
end #

delimiter ;

call populate_posts_vinder_db();

drop procedure if exists populate_tags_vinder_db;

delimiter #
create procedure populate_tags_vinder_db()
begin

declare t_max int unsigned default 1000;
declare t_counter int unsigned default 1;

  start transaction;
  while t_counter < t_max do
    INSERT INTO Tags (Name, PostID) VALUES ('cs201', t_counter);
    INSERT INTO Tags (Name, PostID) VALUES ('ee109', t_counter);
    INSERT INTO Tags (Name, PostID) VALUES ('cs270', t_counter);
    INSERT INTO Tags (Name, PostID) VALUES ('cs104', t_counter);
    INSERT INTO Tags (Name, PostID) VALUES ('cs109', t_counter);
    set t_counter=t_counter+1;
  end while;
  commit;
end #

delimiter ;

call populate_tags_vinder_db();