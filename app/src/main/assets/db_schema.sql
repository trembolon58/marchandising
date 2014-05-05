  CREATE TABLE [works] (
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [user_id] INTEGER,
    [work_id] INTEGER KEY,
    [client_id] INTEGER,
    [shop_id] INTEGER,
    [date] INTEGER,
    [desc] TEXT);

  CREATE TABLE [shops] (
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [shop_id] INTEGER,
    [need_order] INTEGER DEFAULT -1,
    [name] [VARCHAR(255)],
    [shop_address] [VARCHAR(255)]);

  CREATE TABLE [photos] (
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [shop_id] INTEGER,
    [client_id] INTEGER,
    [user_id] INTEGER,
    [path] [VARCHAR(255)]);

  CREATE TABLE [clients] (
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] [VARCHAR(255)],
    [phone] [VARCHAR(50)],
    [client_id] INTEGER KEY);

  CREATE TABLE [orders] (
      [date] INTEGER,
      [id] INTEGER PRIMARY KEY AUTOINCREMENT,
      [goods_id] INTEGER,
      [user_id] INTEGER DEFAULT -1,
      [client_id] INTEGER DEFAULT -1,
      [shop_id] INTEGER DEFAULT -1,
      [shop_name] [VARCHAR(50)],
      [need_order] INTEGER,
      [order_number] INTEGER);

  CREATE TABLE [goods] (
    [date] INTEGER,
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [goods_id] INTEGER,
    [shop_name] [VARCHAR(50)],
    [company] [VARCHAR(50)],
    [name] [VARCHAR(50)],
    [user_id] INTEGER DEFAULT -1,
    [client_id] INTEGER DEFAULT -1,
    [shop_id] INTEGER DEFAULT -1,
    [face] INTEGER,
    [vicyak] INTEGER,
    [return] INTEGER,
    [residue] INTEGER,
    [weight] [VARCHAR(255)],
    [format] [VARCHAR(255)],
    [cost] [VARCHAR(255)]);