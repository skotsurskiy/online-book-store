insert into users (id, email, password, first_name, last_name, shipping_address,
                                     is_deleted)
values (1, 'email@email.com', 'password', 'first', 'last', 'address', false);
insert into roles(id, role)
values (1, 'ROLE_USER');
insert into users_roles(user_id, role_id)
values (1, 1);
insert into shopping_carts (id, is_deleted)
values (1, false);