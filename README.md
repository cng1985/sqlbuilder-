sqlbuilder 通过修改Apache 的dbutils 和sqlbuilder整合进去的工程，包括数据crud功能 


简单的例子
Table表名 Column行名
    package com.benshuixuan.entity;
    
    import java.io.Serializable;
    
    import com.ada.sqlbuilder.Model;
    import com.ada.sqlbuilder.annotation.Column;
    import com.ada.sqlbuilder.annotation.Table;
    @Table(name="article_attr")
    public class ArticleAttr extends Model implements Serializable {
    
    	@Column(name="attr_name")
    	private String name;
    	@Column(name="attr_value")
    	private String value;
    	public String getName() {
    		return name;
    	}
    	public void setName(String name) {
    		this.name = name;
    	}
    	public String getValue() {
    		return value;
    	}
    	public void setValue(String value) {
    		this.value = value;
    	}
    	public Long getArticle_id() {
    		return article_id;
    	}
    	public void setArticle_id(Long article_id) {
    		this.article_id = article_id;
    	}
    	@Column(name="article_id")
    	private Long article_id;
    	@Override
    	public String toString() {
    		return "ArticleAttr [name=" + name + ", id=" + getId()+ ", value=" + value
    				+ ", article_id=" + article_id + "]";
    	}
    	
    }

dao类


    package com.benshuixuan.dao;
    
    import com.ada.sqlbuilder.dao.DataModelDao;
    import com.benshuixuan.entity.ArticleAttr;
    
    public class ArticleAttrDao extends DataModelDao<ArticleAttr> {
    
    	@Override
    	protected Class<ArticleAttr> getEntityClass() {
    		// TODO Auto-generated method stub
    		return ArticleAttr.class;
    	}
    
    }



测试类

    public class ArticleAttrDaoTest {
    
    	ArticleAttrDao dao;
    
    	@Before
    	public void setUp() throws Exception {
    		dao = ObjectFactory.get().getBean(ArticleAttrDao.class);
    	}
    
    	
    	public void test() {
    
    		for (int i = 0; i < 50; i++) {
    			ArticleAttr entity = new ArticleAttr();
    			entity.setName("df");
    			entity.setValue("ff");
    			entity.setArticle_id(53l);
    			long id = dao.add(entity);
    			System.out.println(id);
    		}
    
    	}
    	
    	public void update() {
    
    		ArticleAttr entity = dao.findById(10);
    		System.out.println(entity);
    		entity.setName("d");
    		dao.update(entity);
    		entity = dao.findById(10);
    		System.out.println(entity);
    	}
    	@Test
    	public void testpage() {
    
    		SelectCreator creator = new SelectCreator();
    		creator.from("article_attr");
    		int pageNo =1;
    		int pageSize = 10;
    		Pagination p = dao.pageBySelect(creator, pageNo, pageSize,
    				new BeanListMHandler<ArticleAttr>(ArticleAttr.class));
    		System.out.println(p);
    		List<ArticleAttr> as = (List<ArticleAttr>) p.getList();
    		for (ArticleAttr article : as) {
    			System.out.println(article);
    		}
    	}
    }
    
    