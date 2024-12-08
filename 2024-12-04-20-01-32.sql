-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: emp_ms
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dep_name` varchar(255) NOT NULL,
  `manager_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mag_id` (`manager_id`),
  CONSTRAINT `department_ibfk_1` FOREIGN KEY (`manager_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES (1,'财政部',8001),(2,'人事部',8002),(3,'市场部',8004),(4,'生产部',8003),(5,'信息部',8074);
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `passwd` varchar(255) NOT NULL,
  `sex` char(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `rate` int NOT NULL,
  `birth` date NOT NULL,
  `address` varchar(255) NOT NULL,
  `cardId` varchar(20) NOT NULL,
  `tel` varchar(20) NOT NULL,
  `dep_id` int NOT NULL,
  `job_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_emp_id` (`id`),
  KEY `dep_id` (`dep_id`),
  KEY `job_id` (`job_id`),
  CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`dep_id`) REFERENCES `department` (`id`),
  CONSTRAINT `employee_ibfk_2` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`),
  CONSTRAINT `employee_chk_1` CHECK (((`sex` = _utf8mb3'男') or (`sex` = _utf8mb3'女')))
) ENGINE=InnoDB AUTO_INCREMENT=8389 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (8001,'佚名w','1234','男',0,'2002-11-09','吉林省','220182200001013213','18245644564',1,1),(8002,'佚名f','123456123456','男',1,'2002-04-16','吉林省','318153987998798799','15464656546',2,10),(8003,'佚名l','123456123456','男',0,'2000-08-03','吉林省','532730198708246240','18298390167',4,23),(8004,'佚名wq','123456123456','男',0,'1972-08-05','吉林省','152301197208059650','18995513128',3,16),(8005,'俟司信茜','123456','女',0,'1988-02-06','四川省阿坝藏族羌族自治州','513200198802062466','18995513128',3,17),(8006,'明中厚','123456','男',0,'1997-06-15','广西壮族自治区桂林市雁山区','450311199706156892','18995513128',4,29),(8007,'芮歌永','123456','男',0,'1991-06-17','湖北省武汉市黄陂区','420116199106174398','18995513128',4,29),(8008,'康世','123456','男',0,'1985-10-16','湖北省武汉市青山区','420107198510169776','18995513128',1,7),(8009,'束凯茜','123123','女',0,'1982-02-23','西藏自治区阿里地区札达县','542522198202233566','18995513128',4,29),(8010,'于单娜','123456','女',0,'1971-09-16','河北省承德市滦平县','130824197109169244','18995513128',5,33),(8011,'景园','123456','女',0,'1988-03-28','山东省菏泽地区单县','372925198803289740','18995513128',1,7),(8012,'卫芸','123456','女',0,'1983-06-17','河北省沧州市孟村回族自治县','130930198306179545','18995513128',4,28),(8013,'胡信美','123456','女',0,'1999-08-12','江苏省南通市港闸区','320611199908127763','18995513128',1,3),(8014,'武筠','123456','女',0,'1995-10-28','西藏自治区林芝地区波密县','542625199510287020','18995513128',4,26),(8015,'马玉以','123456','男',0,'1990-09-07','河北省邢台市市辖区','130501199009078837','18995513128',5,33),(8016,'乔加月','123456','女',0,'1980-09-15','山西省长治市黎城县','140426198009153103','18995513128',1,6),(8017,'毛琬','123456','女',0,'1994-09-01','四川省巴中地区','513700199409016860','18995513128',5,35),(8018,'彭家辉','124242','男',0,'1996-10-17','广东省梅州市兴宁市','441481199610173712','18995513128',2,15),(8019,'汪被行','123456','男',0,'1994-11-07','湖南省益阳市桃江县','430922199411075874','18995513128',1,3),(8020,'骆九风','123456','男',0,'1996-04-10','福建省福州市','350100199604108853','18995513128',5,31),(8021,'俞蕊','123456','女',0,'1977-09-24','辽宁省铁岭市昌图县','211224197709247064','18995513128',5,35),(8022,'松瑶','123456','女',0,'1982-04-05','黑龙江省大庆市萨尔图区','230602198204053003','18995513128',4,4),(8023,'孙兴','123456','男',0,'1978-06-11','江苏省盐城市大丰市','32098219780611709X','18995513128',2,13),(8024,'强钰娴','031214','女',0,'1970-06-16','湖南省湘潭市','430300197006152987','18995513128',5,37),(8025,'贺翔','123456','男',0,'1992-02-13','湖南省邵阳市绥宁县','430527199202136538','18995513128',5,31),(8026,'郁马萍','123456','女',0,'1981-07-25','四川省泸州市泸县','510521198107259705','18995513128',1,8),(8027,'尹笑敬','123456','男',0,'1985-12-01','山东省德州市临邑县','371424198512014419','18995513128',4,27),(8028,'邢器平','123456','男',0,'1970-11-08','山西省太原市尖草坪区','140108197011087770','18995513128',4,29),(8029,'步妍','123456','女',0,'1976-12-18','广东省汕头市潮阳市','44058219761218904X','18995513128',5,31),(8030,'宣静','123456','女',0,'1972-01-13','四川省绵阳市安县','510724197201131666','18995513128',3,19),(8031,'葛秀','123456','女',0,'1987-01-18','广西壮族自治区柳州地区武宣县','452225198701186283','18995513128',1,8),(8032,'车轮','123456','男',0,'1984-11-27','江西省宜春地区','362200198411271798','18995513128',1,8),(8033,'于都雅','123456','女',0,'1993-07-28','北京市市辖区','110100199307283802','18995513128',4,24),(8034,'都无爱','123456','女',0,'1987-02-16','河北省邢台市临西县','130535198702168223','18995513128',4,26),(8035,'茹凯振','123456','男',0,'1973-01-04','安徽省阜阳市颍泉区','34120419730104665X','18995513128',1,8),(8036,'冀栋','123456','男',0,'1974-06-13','安徽省淮北市相山区','340603197406137739','18995513128',1,7),(8037,'百里婵','123456','女',0,'1993-02-21','黑龙江省绥化地区海伦市','232304199302213368','18995513128',4,26),(8038,'崔忠慧','123456','女',0,'1975-12-24','安徽省黄山市屯溪区','341002197512248821','18995513128',2,14),(8039,'束贝菊','123456','女',0,'1986-07-27','江苏省徐州市泉山区','320311198607278268','13140375456',2,14),(8040,'逄泽','123456','男',0,'1999-05-29','宁夏回族自治区银川市市辖区','640101199905293998','18995513128',2,15),(8041,'干百卡坚','123456','男',0,'1980-02-09','黑龙江省双鸭山市友谊县','230522198002096013','18995513128',3,18),(8042,'刁哲','123456','男',0,'1974-01-08','陕西省延安市宝塔区','610602197401086054','18995513128',3,18),(8043,'伊好翠','123456','女',0,'1970-01-23','湖北省省直辖县级行政单位','429000197001232267','18995513128',5,35),(8044,'昌上江','123456','男',0,'1988-08-11','广东省肇庆市德庆县','44122619880811585X','18995513128',4,25),(8045,'公晓','123456','女',0,'1982-12-31','浙江省绍兴市诸暨市','330681198212318584','18995513128',3,17),(8046,'尚贝瑾','123456','女',0,'1992-03-19','辽宁省铁岭市西丰县','211223199203193065','18995513128',2,14),(8047,'关智娣','123456','女',0,'1989-02-02','四川省南充市仪陇县','511324198902024680','18995513128',5,37),(8048,'康翰','123456','男',0,'1977-09-17','天津市静海县','120223197709171072','13140375456',2,15),(8049,'红芝','123456','女',0,'1985-02-08','云南省楚雄彝族自治州双柏县','53232219850208116X','13140375456',5,30),(8050,'解钰光','123456','男',0,'1977-06-03','吉林省吉林市蛟河市','220281197706036550','18995513128',5,34),(8051,'万仁壮','123456','男',0,'1999-04-29','吉林省长春市市辖区','220101199904295731','18995513128',3,18),(8052,'邴智丽','123456','女',0,'1980-08-19','陕西省商洛地区柞水县','612527198008194807','18995513128',4,29),(8053,'包瑞','123456','女',0,'1972-06-14','江西省赣州市章贡区','36070219720614868X','18995513128',1,7),(8054,'花九武','123456','男',0,'1974-02-20','广西壮族自治区南宁地区马山县','452127197402208676','18995513128',5,35),(8055,'茅马彩','123456','女',0,'1997-04-25','黑龙江省牡丹江市爱民区','231004199704254405','18995513128',3,22),(8056,'秦贝震','123456','男',0,'1998-06-10','福建省南平市','350700199806103252','18995513128',3,17),(8057,'宫南凯婵','123456','女',0,'2000-06-29','新疆维吾尔族自治区伊犁哈萨克自治州伊犁地区','654100200006297842','18995513128',1,8),(8058,'岑上香','123456','女',0,'1981-03-11','四川省宜宾市市辖区','511501198103114563','18995513128',5,34),(8059,'通礼琴','123456','女',0,'1991-10-02','云南省曲靖市富源县','530325199110025507','18995513128',5,33),(8060,'俞中竹','123456','女',0,'1984-10-28','福建省厦门市','350200198410283726','18995510000',4,4),(8061,'游顺','123456','男',0,'1994-04-11','甘肃省平凉地区泾川县','622722199404111159','18995513128',1,8),(8062,'隗斌','123456','男',0,'1982-08-13','山西省晋城市陵川县','140524198208132877','18995513128',3,18),(8063,'牧超','123456','男',0,'1994-11-04','广西壮族自治区玉林市陆川县','450922199411047738','18995513128',3,20),(8064,'包卡晨','123456','男',0,'1985-07-14','贵州省贵阳市花溪区','520111198507145990','18995513128',4,28),(8065,'平亨','123456','男',0,'1973-07-06','四川省成都市武侯区','510107197307068133','18995513128',3,16),(8066,'劳金荷','123456','女',0,'1981-07-06','安徽省淮南市市辖区','340401198107067065','18995513128',3,22),(8067,'袁卡兴','123456','男',0,'1976-03-25','江苏省镇江市扬中市','321182197603257671','18995513128',3,19),(8068,'卜家','123456','男',0,'1976-04-21','陕西省宝鸡市眉县','610326197604218138','18995513128',1,6),(8069,'洪孝之','123456','男',0,'1985-12-07','河北省沧州市新华区','130902198512073877','18995513128',4,4),(8070,'刁都融','123456','女',0,'2000-12-06','新疆维吾尔族自治区昌吉回族自治州','652300200012065449','18995513128',3,19),(8071,'秦仁艺','123456','女',0,'1991-08-28','天津市红桥区','120106199108281345','18995513128',1,2),(8072,'富凯珍','123456','女',0,'1980-03-02','河南省鹤壁市','410600198003027766','18995513128',4,28),(8073,'江中岚','123456','女',0,'1971-02-20','吉林省辽源市','220400197102204544','18995513128',5,32),(8074,'祁晶','123456','女',0,'1981-10-08','黑龙江省鹤岗市工农区','230403198110085181','18995513128',3,21),(8075,'尹忠山','123456','男',0,'1996-05-02','四川省泸州市江阳区','510502199605029676','13140375456',2,13),(8076,'邬裕','123456','男',0,'1972-02-27','山西省晋城市市辖区','140601197202279911','18995513128',5,39),(8077,'昌上家','123456','男',0,'1990-09-27','黑龙江省齐齐哈尔市碾子山区','230207199009273718','13140375456',5,36),(8078,'解露','123456','女',0,'1988-09-12','新疆维吾尔族自治区喀什地区喀什市','653101198809126647','18995513128',3,19),(8079,'方赫祥','123456','男',0,'1979-07-18','河南省濮阳市范县','410926197907183257','18995513128',4,29),(8080,'滑礼风','123456','男',0,'1991-09-26','黑龙江省大庆市让胡路区','230604199109261610','18995513128',4,27),(8081,'陶忠露','123456','女',0,'1974-01-08','四川省甘孜藏族自治州九龙县','513324197401081326','18995513128',3,17),(8082,'裴仁朗','123456','男',0,'1974-10-31','辽宁省鞍山市千山区','210311197410319855','18995513128',1,8),(8083,'陈上健','123456','男',0,'1978-03-12','安徽省马鞍山市向山区','340505197803125157','18995513128',1,5),(8084,'万杰','123456','男',0,'1982-07-27','湖南省娄底地区涟源市','432503198207279032','18995513128',4,26),(8085,'黄珠','123456','女',0,'1977-10-01','黑龙江省哈尔滨市呼兰县','230121197710017088','18995513128',5,33),(8086,'路燕','123456','女',0,'1989-12-29','山西省临汾地区大宁县','142633198912296625','18995513128',2,15),(8087,'孙慕浩','123456','男',0,'1983-04-16','浙江省温州市洞头县','33032219830416837X','18995513128',2,15),(8088,'通风','123456','男',0,'1975-06-03','内蒙古自治区呼和浩特市土默特左旗','150121197506036177','18995513128',5,36),(8089,'何芬','123456','女',0,'1973-08-06','重庆市石柱土家族自治县','500240197308064820','18995513128',5,35),(8090,'羊凯珍','123456','女',0,'1973-04-24','陕西省安康地区','612400197304243749','18995513128',1,8),(8091,'毕卡磊','123456','男',0,'1973-06-15','广东省阳江市江城区','441702197306152118','13140375456',3,16),(8092,'贾贝莲','123456','女',0,'1973-08-07','山西省阳泉市矿区','140303197308077046','18995513128',1,6),(8093,'安东','123456','男',0,'1995-02-01','湖南省株洲市石峰区','430204199502015251','18995513128',5,31),(8094,'史友策','123456','男',0,'1978-10-02','河北省保定市市辖区','130601197810023475','18995513128',5,36),(8095,'陶泽','123456','男',0,'1995-11-01','广东省韶关市南雄市','440282199511019335','18995513128',1,2),(8096,'王加琼','123456','女',0,'1972-04-07','湖南省邵阳市市辖区','430501197204072045','18995513128',5,35),(8097,'禹凯敬','123456','男',0,'1979-10-14','安徽省淮南市','340400197910149093','18995513128',4,29),(8098,'宗都姬','123456','女',0,'1995-01-27','青海省黄南藏族自治州尖扎县','632322199501271883','18995513128',3,19),(8099,'邵金婷','123456','女',0,'1990-07-13','山东省德州市武城县','371428199007134620','13140375456',3,18),(8100,'宣孝丽','123456','女',0,'1995-01-15','四川省甘孜藏族自治州泸定县','513322199501151340','18995513128',4,26),(8101,'卜电广','123456','男',0,'1983-02-13','四川省南充市西充县','51132519830213679X','18995513128',2,11),(8102,'石德','123456','男',0,'1999-06-09','浙江省衢州市市辖区','330801199906093993','18995513128',3,20),(8103,'顾被思','123456','男',0,'1993-08-23','河南省商丘市虞城县','411425199308231313','18995513128',2,14),(8104,'宰义良','123456','男',0,'1989-10-10','河北省邯郸市邱县','130430198910105430','18995513128',5,39),(8105,'呼延都达','123456','男',0,'1973-08-29','吉林省辽源市东辽县','22042219730829693X','18995513128',2,15),(8106,'盖栋','123456','男',0,'1971-01-17','广西壮族自治区桂林市龙胜各族自治县','45032819710117317X','18995513128',1,6),(8107,'单于金影','123456','女',0,'1971-11-29','江西省南昌市安义县','360123197111295109','18995513128',4,24),(8108,'杭蓉','123456','女',0,'1999-12-30','云南省怒江傈僳族自治州','533300199912305565','18995513128',3,17),(8109,'严健','123456','男',0,'1977-04-17','河南省周口地区郸城县','412726197704176816','18995513128',3,20),(8110,'鱼义眉','123456','女',0,'1972-01-27','河北省保定市顺平县','130636197201278544','18995513128',1,2),(8111,'闾丘凯瑞','123456','女',0,'1970-06-28','黑龙江省哈尔滨市方正县','230124197006281263','18995513128',5,30),(8112,'吴礼飘','123456','女',0,'1983-10-31','四川省攀枝花市市辖区','510401198310316743','18995513128',5,36),(8113,'巫马好炎','123456','男',0,'1974-05-21','广西壮族自治区贺州地区贺州市','452402197405213154','18995513128',1,2),(8114,'戈都子','123456','男',0,'1989-02-10','宁夏回族自治区吴忠市中宁县','640322198902108337','18995513128',4,28),(8115,'步姣','123456','女',0,'1990-08-25','河北省张家口市涿鹿县','130731199008253583','18995513128',4,26),(8116,'危孝龙','123456','男',0,'1974-04-25','四川省成都市龙泉驿区','510112197404257439','18995513128',1,7),(8117,'苗无光','123456','男',0,'1976-04-18','贵州省铜仁地区江口县','52222219760418239X','13140375456',2,11),(8118,'韩薇','123456','女',0,'1974-03-15','贵州省黔南布依族苗族自治州龙里县','522730197403156440','18995513128',5,35),(8119,'红被思','123456','男',0,'1995-09-22','江西省宜春地区宜春市','362201199509223015','18995513128',5,33),(8120,'公好固','123456','男',0,'1981-03-16','江苏省连云港市','320700198103168236','18995513128',2,15),(8121,'羊澹昭','123456','女',0,'1971-02-24','湖北省荆州市松滋市','421087197102241604','18995513128',1,3),(8122,'车玉','123456','女',0,'1995-01-16','山西省忻州地区柳林县','142327199501165867','18995513128',5,30),(8123,'万俟聪','123456','女',0,'1979-02-03','西藏自治区山南地区措美县','542227197902033582','18995513128',1,9),(8124,'跋夹青','123456','女',0,'1986-03-25','浙江省嘉兴市海盐县','330424198603253943','13140375456',1,3),(8125,'刘器飘','123456','女',0,'1983-11-13','山西省忻州地区文水县','142322198311135468','18995513128',5,35),(8126,'耿中','123456','男',0,'1987-01-01','山西省晋城市应县','140622198701014199','18995513128',3,18),(8127,'梁丘电羽','123456','女',0,'1993-02-02','江苏省连云港市海州区','320706199302028060','18995513128',2,15),(8128,'诸加玲','123456','女',0,'1971-04-21','辽宁省抚顺市顺城区','210411197104213644','18995513128',3,19),(8129,'向忠海','123456','男',0,'1991-08-06','贵州省铜仁地区思南县','522225199108066099','18995513128',1,9),(8130,'余冠','123456','男',0,'1994-02-04','山西省晋城市阳城县','140522199402047854','18995513128',5,39),(8131,'邱芳','123456','女',0,'1980-06-29','河南省信阳市罗山县','411521198006292685','18995513128',4,25),(8132,'万俟凯轮','123456','男',0,'1974-12-17','河南省平顶山市叶县','410422197412176319','18995513128',3,19),(8133,'谢易宏','123456','男',0,'1993-01-08','广东省云浮市市辖区','445301199301085299','18995513128',4,28),(8134,'时加安','123456','男',0,'1980-03-15','安徽省淮南市八公山区','340405198003151372','18995513128',3,22),(8135,'韩荔','123456','女',0,'1975-03-19','云南省怒江傈僳族自治州福贡县','533323197503192323','18995513128',5,34),(8136,'娄加亚','123456','女',0,'1994-10-02','新疆维吾尔族自治区博尔塔拉蒙古自治州精河县','652722199410026060','13140375456',1,2),(8137,'唐好子','123456','男',0,'1982-09-01','安徽省蚌埠市','340300198209016232','18995513128',1,9),(8138,'向歌思','123456','女',0,'1983-01-20','山西省忻州地区吕梁地区','142300198301202761','18995513128',4,4),(8139,'许承','123456','男',0,'1971-09-27','内蒙古自治区乌兰察布盟集宁市','152601197109277413','18995513128',5,35),(8140,'滕被伯','123456','男',0,'1980-06-06','广西壮族自治区桂林市','450300198006066630','18995513128',3,21),(8141,'韶凯哲','123456','男',0,'1987-09-29','安徽省黄山市','341000198709293177','18995513128',2,15),(8142,'井义','123456','男',0,'1979-08-25','河北省邯郸市广平县','130432197908257273','18995513128',5,36),(8143,'蒋智翰','123456','男',0,'1991-01-06','河南省平顶山市卫东区','410403199101062891','18995513128',3,21),(8144,'习笑腾','123456','男',0,'1980-04-03','江苏省常州市金坛市','32048219800403103X','18995513128',1,9),(8145,'陈菁','123456','女',0,'1996-05-16','湖南省长沙市芙蓉区','43010219960516848X','18995513128',1,6),(8146,'罗德','123456','男',0,'1975-03-08','黑龙江省绥化地区兰西县','232325197503085211','18995513128',3,20),(8147,'殷翰','123456','男',0,'1987-02-14','内蒙古自治区伊克昭盟伊金霍洛旗','152728198702142851','18995513128',1,3),(8148,'彭上昌','123456','男',0,'1999-07-27','吉林省通化市通化县','220521199907279275','18995513128',5,30),(8149,'靳咏','123456','女',0,'1972-01-23','河北省秦皇岛市市辖区','130301197201238466','18995513128',1,2),(8150,'吴振','123456','男',0,'1992-10-01','浙江省衢州市','330800199210014736','18995513128',4,27),(8151,'吉云','123456','女',0,'1984-02-22','河北省保定市顺平县','130636198402227828','13140375456',5,33),(8152,'耿冠','123456','男',0,'1997-02-05','吉林省通化市市辖区','220501199702055432','18995513128',1,8),(8153,'殴才','123456','男',0,'1978-07-24','江西省上饶地区上饶市','362301197807249156','18995513128',5,38),(8154,'葛闻珍','123456','女',0,'1997-04-19','江西省南昌市','36010019970419448X','18995513128',3,17),(8155,'驷公彬','123456','男',0,'1998-02-02','福建省三明市建宁县','350430199802028074','18995513128',1,6),(8156,'贺心','123456','男',0,'1997-04-02','甘肃省甘南藏族自治州临潭县','623021199704022111','18995513128',1,9),(8157,'萧策','123456','男',0,'1988-10-15','黑龙江省大庆市红岗区','230605198810151753','18995513128',4,28),(8158,'金山','123456','男',0,'1995-12-24','贵州省铜仁地区沿河土家族自治县','522228199512243236','13140375456',3,16),(8159,'伍金璧','123456','女',0,'1973-03-07','湖南省衡阳市市辖区','430401197303071843','18995513128',5,39),(8160,'黄易融','123456','女',0,'1993-05-18','内蒙古自治区伊克昭盟达拉特旗','152722199305185344','18995513128',1,4),(8161,'符先','123456','男',0,'1974-10-05','黑龙江省鸡西市滴道区','230304197410056354','18995513128',1,4),(8162,'步辉','123456','男',0,'1994-05-03','湖南省株洲市石峰区','430204199405036894','18995513128',3,21),(8163,'龙马军','123456','男',0,'1995-04-12','四川省成都市都江堰市','510181199504127254','18995513128',1,3),(8164,'牧智芳','123456','女',0,'1998-09-12','河南省南阳市唐河县','411328199809127809','18995513128',5,33),(8165,'金礼芸','123456','女',0,'1983-02-24','广西壮族自治区桂林市荔浦县','450331198302248185','18995513128',5,34),(8166,'冶宗晶','123456','女',0,'1992-10-23','湖南省怀化市中方县','431221199210231224','18995513128',4,26),(8167,'耿发','123456','男',0,'1973-11-16','河南省洛阳市偃师市','410381197311161032','18995513128',4,26),(8168,'东朋','123456','男',0,'1983-09-17','广东省韶关市浈江区','440204198309171831','18995513128',1,6),(8169,'潘上君','123456','女',0,'1970-03-11','安徽省滁州市市辖区','341101197003118008','18995513128',4,25),(8170,'司空枫','123456','女',0,'1980-06-27','江西省上饶地区上饶市','362301198006276861','18995513128',5,36),(8171,'冯被荔','123456','女',0,'1984-01-26','湖北省襄樊市南漳县','420624198401269085','18995513128',5,34),(8172,'龙卡蓉','123456','女',0,'1998-07-07','山东省菏泽地区单县','372925199807079921','18995513128',5,38),(8173,'夹谷玉平','123456','男',0,'1975-11-03','浙江省丽水地区','332500197511032870','18995513128',3,16),(8174,'祖国','123456','男',0,'1982-02-09','湖北省孝感市市辖区','420901198202092813','18995513128',5,31),(8175,'万卡雅','123456','女',0,'1998-07-30','内蒙古自治区赤峰市翁牛特旗','150426199807306609','18995513128',1,9),(8176,'邓生','123456','男',0,'1992-01-09','四川省绵阳市盐亭县','510723199201099515','13140375456',1,7),(8177,'孙金香','123456','女',0,'1976-05-20','安徽省蚌埠市','340300197605203502','18995513128',3,16),(8178,'司空马春','123456','女',0,'1986-04-01','河南省洛阳市市辖区','410301198604011129','18995513128',1,6),(8179,'杭锦','123456','女',0,'1974-02-27','江西省赣州市安远县','360726197402271264','18995513128',1,2),(8180,'劳弘','123456','男',0,'1973-02-14','湖南省常德市临澧县','430724197302146779','18995513128',2,15),(8181,'成好素','123456','女',0,'1989-01-01','浙江省宁波市','330200198901014885','18995513128',3,19),(8182,'祝都萍','123456','女',0,'1985-02-21','江苏省苏州市吴县市','320586198502216302','13003459385',4,26),(8183,'葛钰峰','123456','男',0,'1989-04-20','新疆维吾尔族自治区克孜勒苏柯尔克孜自治州阿合奇县','653023198904208053','18995513128',5,35),(8184,'卫民','123456','男',0,'1984-10-14','江西省赣州市石城县','360735198410147694','13140375456',1,9),(8185,'安梅','123456','女',0,'1992-05-30','黑龙江省牡丹江市东安区','231002199205308162','18995513128',1,7),(8186,'吴勇','123456','男',0,'1972-03-24','贵州省遵义市正安县','520324197203249713','18995513128',5,38),(8187,'崔广','123456','男',0,'1983-12-10','黑龙江省大兴安岭地区塔河县','232722198312109775','18995513128',3,22),(8188,'夏笑涛','123456','男',0,'1973-04-06','甘肃省张掖地区','622200197304063351','18995513128',1,3),(8189,'洪克','123456','男',0,'1986-02-25','山东省济宁市梁山县','370832198602251750','18995513128',1,7),(8190,'姜易和','123456','男',0,'1998-07-09','河北省保定市唐县','13062719980709593X','18995513128',3,17),(8191,'弓林','123456','男',0,'1989-11-27','黑龙江省哈尔滨市','230100198911276513','18995513128',4,27),(8192,'富爽','123456','女',0,'1983-07-07','广西壮族自治区百色地区隆林各族自治县','452631198307076202','18995513128',3,18),(8193,'顾伟','123456','男',0,'1999-11-23','内蒙古自治区赤峰市红山区','150402199911238633','18995513128',2,14),(8194,'寇仉腾','123456','男',0,'1987-12-10','江西省九江市永修县','36042519871210819X','18995513128',5,36),(8195,'狐钟金洁','123456','女',0,'1979-11-29','湖北省荆门市钟祥市','420881197911293628','18995513128',5,36),(8196,'伍兴','123456','男',0,'1985-08-20','安徽省合肥市长丰县','340121198508208796','18995513128',3,22),(8197,'甄友毅','123456','男',0,'1987-08-27','云南省楚雄彝族自治州南华县','532324198708277937','18995513128',4,28),(8198,'文时','123456','男',0,'1972-08-17','黑龙江省绥化地区青冈县','232326197208171455','18995513128',3,19),(8199,'邹仁毅','123456','男',0,'1998-02-20','河北省保定市南市区','130604199802206130','18995513128',2,15),(8200,'滕玉成','123456','男',0,'1988-11-13','安徽省芜湖市','340200198811132871','18995513128',2,15),(8201,'曹月','123456','女',0,'1993-12-21','辽宁省丹东市市辖区','210601199312216169','18995513128',5,34),(8202,'包竹','123456','女',0,'1991-12-15','河北省唐山市丰润县','130221199112151827','18396732250',5,36),(8203,'康世','123456','男',0,'1970-01-16','辽宁省丹东市市辖区','210601199312516168','15874568734',2,15),(8386,'路燕','123456','女',0,'1989-12-29','山西省临汾地区大宁县','142633198912296625','18995513128',2,15),(8387,'test22','123123','男',0,'2002-02-12','456465465','12321321321','45465454',2,8),(8388,'test','test','男',0,'2022-05-07','test','202123555566664444','13113311331',2,1);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job`
--

DROP TABLE IF EXISTS `job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job` (
  `id` int NOT NULL AUTO_INCREMENT,
  `job_name` varchar(255) NOT NULL,
  `dep_id` int NOT NULL,
  `salary` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `job_ibfk_1` (`dep_id`),
  CONSTRAINT `job_ibfk_1` FOREIGN KEY (`dep_id`) REFERENCES `department` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `job_chk_1` CHECK ((`salary` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job`
--

LOCK TABLES `job` WRITE;
/*!40000 ALTER TABLE `job` DISABLE KEYS */;
INSERT INTO `job` VALUES (1,'财政部主管',1,30000),(2,'首席财务官',1,26000),(3,'总会计师',1,24999),(4,'财务总监',1,22000),(5,'资金总监',1,20000),(6,'财务部经理',1,18000),(7,'审计主管',1,15000),(8,'会计',1,12000),(9,'助理',1,10000),(10,'人事部主管',2,30000),(11,'人事经理',2,25000),(12,'人事部总监',2,28000),(13,'公关代表',2,20000),(14,'行政助理',2,15000),(15,'事务员',2,10001),(16,'市场部主管',3,30000),(17,'采购经理',3,12000),(18,'销售经理',3,15000),(19,'销售代表',3,11200),(20,'运输文员',3,8000),(21,'库存文员',3,8000),(22,'库存经理',3,10000),(23,'生产部主管',4,30001),(24,'车间主任',4,9001),(25,'机电组长',4,9002),(26,'生产管理员',4,9000),(27,'生产组长',4,9000),(28,'生产技术员',4,10000),(29,'生产技工',4,12000),(30,'运维员',5,10000),(31,'信息经理',5,12000),(32,'信息部主管',5,30000),(33,'前端主管',5,20000),(34,'后端主管',5,20000),(35,'程序员',5,12000),(36,'网络工程师',5,12000),(37,'网络主管',5,25000),(38,'网络经理',5,24000),(39,'网络专员',5,10020),(40,'测试职位',1,12388);
/*!40000 ALTER TABLE `job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manager`
--

DROP TABLE IF EXISTS `manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manager` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `passwd` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manager`
--

LOCK TABLES `manager` WRITE;
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` VALUES (1001,'leo','1234');
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `view_all_dep_info`
--

DROP TABLE IF EXISTS `view_all_dep_info`;
/*!50001 DROP VIEW IF EXISTS `view_all_dep_info`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `view_all_dep_info` AS SELECT 
 1 AS `部门编号`,
 1 AS `部门名称`,
 1 AS `部门主管编号`,
 1 AS `部门主管名称`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `view_all_dep_info`
--

/*!50001 DROP VIEW IF EXISTS `view_all_dep_info`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_all_dep_info` AS select `d`.`id` AS `部门编号`,`d`.`dep_name` AS `部门名称`,`e`.`id` AS `部门主管编号`,`e`.`name` AS `部门主管名称` from (`department` `d` join `employee` `e` on((`d`.`manager_id` = `e`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-04 20:01:32