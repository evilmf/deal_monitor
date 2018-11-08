package com.apptivedeals.monitor.dao;

public class Queries {
	public static final String GET_LATEST_SNAPSHOT_NO_DETAIL = "SELECT " + "  id, " + "  create_date, "
			+ "  update_date, " + "  is_active " + "FROM snapshots " + "ORDER BY 1 DESC " + "LIMIT 1";

	public static final String GET_LATEST_SNAPSHOT = "SELECT " + "  id, " + "  create_date, " + "  update_date, "
			+ "	 is_active, " + "  snapshot " + "FROM snapshots " + "ORDER BY 1 DESC " + "LIMIT 1";

	public static final String GET_SNAPSHOT_BY_ID = "select " + "id, " + "create_date, " + "update_date, "
			+ "is_active, " + "snapshot " + "from snapshots " + "where id = ?";

	public static final String GET_CURRENT_SNAPSHOT = "select " + "id, " + "product_id, " + "price_regular, "
			+ "price_discount, " + "snapshot_id, " + "is_active, " + "create_date, " + "rank " + "from ( "
			+ "		select " + "		sd.*, "
			+ " 	row_number() over (partition by sd.product_id order by sd.id desc) rank "
			+ " 	from snapshot_detail sd " + " 	join products p on p.id = sd.product_id "
			+ ") t where rank = 1 and is_active = true";

	public static final String INSERT_SNAPSHOT_DETAIL = "insert into snapshot_detail " + "(id, " + "product_id, "
			+ "price_regular, " + "price_discount, " + "snapshot_id, " + "is_active, " + "create_date) "
			+ "values (nextval('seq_snapshot_detail_id'), ?, ?, ?, ?, ?, ?)";

	public static final String GET_NEXT_SNAPSHOT_ID = "select coalesce(max(snapshot_id) + 1, 1) from snapshot_detail";

	public static final String GET_ALL_BRAND = "select " + "id, " + "name, " + "create_date, " + "update_date, "
			+ "is_active " + "from brands";

	public static final String GET_ALL_GENDER = "select " + "id, " + "name, " + "create_date, " + "update_date, "
			+ "is_active " + "from genders";

	public static final String GET_ALL_CATEGORY = "select " + "id, " + "name, " + "create_date, " + "update_date, "
			+ "is_active " + "from categories";

	public static final String PRODUCT_SEARCH = "select distinct " + "p.id, " + "p.name product_name, "
			+ "b.name brand_name, " + "g.name gender_name, " + "c.name category_name, " + "i.url image_url, "
			+ "min(sd.price_discount) over (partition by p.id) min_price, "
			+ "max(sd.price_discount) over (partition by p.id) max_price, "
			+ "count(p.id) over (partition by p.id) count, "
			+ "ts_rank(name_tsvector, plainto_tsquery('simple', ?)) ts_rank " + "from products p "
			+ "join snapshot_detail sd on sd.product_id = p.id " + "join brands b on b.id = p.brand_id "
			+ "join categories c on c.id = p.category_id " + "join images i on i.product_id = p.id "
			+ "join genders g on g.id = p.gender_id " + "where name_tsvector @@ to_tsquery('simple', ?) "
			+ "and sd.is_active = true";

	public static final String GET_SNAPSHOT_LIST_BY_PRODUCT_ID = "with active_snapshot as ( " + "select " + "sd.*, "
			+ "row_number() over (partition by product_id order by create_date) rank " + "from snapshot_detail sd "
			+ "where sd.product_id = ? " + "and sd.is_active = true " + "), " + "inactive_snapshot as ( " + "select "
			+ "sd.*, " + "row_number() over (partition by product_id order by create_date) rank "
			+ "from snapshot_detail sd " + "where sd.product_id = ? " + "and sd.is_active = false " + ") " + "select "
			+ "ass.snapshot_id, " + "ass.product_id, " + "p.name, " + "b.id brand_id, " + "b.name brand_name, "
			+ "g.id gender_id, " + "g.name gender_name, " + "c.id category_id, " + "c.name category_name, "
			+ "ass.price_discount price, " + "ass.create_date active_date, " + "iss.create_date inactive_date, "
			+ "EXTRACT(EPOCH FROM iss.create_date - ass.create_date) duration " + "from active_snapshot ass "
			+ "left join inactive_snapshot iss on iss.rank = ass.rank " + "join products p on p.id = ass.product_id "
			+ "join brands b on b.id = p.brand_id " + "join genders g on g.id = p.gender_id "
			+ "join categories c on c.id = p.category_id " + "order by snapshot_id";

	public static final String GET_PRODUCTS_BY_BRAND_ID = "select " + "id, " + "product_id, " + "category_id, "
			+ "brand_id, " + "gender_id, " + "name, " + "url, " + "create_date, " + "update_date, " + "is_active "
			+ "from products " + "where brand_id = ?";

	public static final String INSERT_PRODUCT = "insert into products " + "(id, " + "product_id, " + "category_id, "
			+ "brand_id, " + "gender_id, " + "name, " + "url, " + "create_date, " + "update_date, " + "is_active) "
			+ "values (nextval('seq_product_id'), ?, ?, ?, ?, ?, ?, ?, ?, ?) " + "returning id";

	public static final String INSERT_IMAGE = "insert into images " + "(id, " + "url, " + "product_id, "
			+ "create_date, " + "update_date, " + "is_active) " + "values (nextval('seq_image_id'), ?, ?, ?, ?, ?)";

	public static final String UPDATE_PRODUCT_URL_BY_ID = "update products " + "set url = ? " + "where id = ?";

	public static final String GET_BRAND_ID_BY_NAME = "select " + "id " + "from brands " + "where name = ?";

	public static final String INSERT_BRAND = "insert into brands " + "(id, " + "name, " + "create_date, "
			+ "update_date, " + "is_active) " + "values (nextval('seq_brand_id'), ?, now(), now(), true) "
			+ "returning id";

	public static final String GET_GENDER_ID_BY_NAME = "select " + "id " + "from genders " + "where name = ?";

	public static final String INSERT_GENDER = "insert into genders " + "(id, " + "name, " + "create_date, "
			+ "update_date, " + "is_active) " + "values (nextval('seq_gender_id'), ?, now(), now(), true) "
			+ "returning id";

	public static final String GET_CATEGORY_ID_BY_NAME = "select " + "id " + "from categories " + "where name = ?";

	public static final String INSERT_CATEGORY = "insert into categories " + "(id, " + "name, " + "create_date, "
			+ "update_date, " + "is_active) " + "values (nextval('seq_category_id'), ?, now(), now(), true) "
			+ "returning id";

	public static final String GET_MISSING_SNAPSHOT_ID = "select " + "distinct sd.snapshot_id "
			+ "from snapshot_detail sd " + "left join snapshots s on s.id = sd.snapshot_id "
			+ "where s.id is null order by 1";

	public static final String GENERATE_SNAPSHOT_BY_ID = "with snapshot_detail_tmp as ( " + "select sd.* from ( "
			+ "select " + "sd.*, " + "row_number() over (partition by product_id order by id desc) rank "
			+ "from snapshot_detail sd " + "where snapshot_id <= ? " + ") sd where sd.rank = 1 and sd.is_active = true "
			+ ") " + "select " + "sd.id, " + "p.id product_id, " + "p.product_id product_data_id, "
			+ "p.create_date product_create_date, " + "p.brand_id, " + "b.name brand, " + "p.gender_id, "
			+ "g.name gender, " + "p.category_id, " + "c.name category, " + "sd.snapshot_id, " + "sd.price_regular, "
			+ "sd.price_discount, " + "round((sd.price_regular - sd.price_discount) / sd.price_regular, 2) discount, "
			+ "case when sd.snapshot_id = ? then true else false end is_new, " + "sd.create_date sd_create_date, "
			+ "p.url prod_url, " + "p.name prod_name, " + "i.url img_url " + "from snapshot_detail_tmp sd "
			+ "join products p on p.id = sd.product_id and p.is_active = true "
			+ "join brands b on b.id = p.brand_id and b.is_active = true "
			+ "join genders g on g.id = p.gender_id and g.is_active = true "
			+ "join categories c on c.id = p.category_id and c.is_active = true "
			+ "left join images i on i.product_id = p.id " + "order by sd.id desc";

	public static final String INSERT_SNAPSHOT = "insert into snapshots "
			+ "(id, create_date, update_date, is_active, snapshot) " 
			+ "values (?, ?, ?, ?, ?)";
}
