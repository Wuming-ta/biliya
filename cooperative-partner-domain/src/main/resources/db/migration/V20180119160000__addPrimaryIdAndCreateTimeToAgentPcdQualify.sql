
DROP table IF EXISTS t_agent_pcd_qualify_temp;
CREATE TABLE IF NOT EXISTS t_agent_pcd_qualify_temp (
    id integer not null primary key auto_increment,
    agent_id integer not null,
    pcd_qualify_id integer not null,
    physical_settlement_percentage integer,
    create_time timestamp null,
    foreign key (agent_id) references t_agent (id) on delete cascade,
    foreign key (pcd_qualify_id) references t_pcd_qualify (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

INSERT INTO t_agent_pcd_qualify_temp(agent_id,pcd_qualify_id, physical_settlement_percentage,create_time) SELECT agent_id,pcd_qualify_id, physical_settlement_percentage,now() as create_time FROM t_agent_pcd_qualify;


drop table t_agent_pcd_qualify;
CREATE TABLE IF NOT EXISTS t_agent_pcd_qualify (
    id integer not null primary key auto_increment,
    agent_id integer not null,
    pcd_qualify_id integer not null,
    physical_settlement_percentage integer,
    create_time timestamp null,
    foreign key (agent_id) references t_agent (id) on delete cascade,
    foreign key (pcd_qualify_id) references t_pcd_qualify (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;


INSERT INTO t_agent_pcd_qualify SELECT * FROM t_agent_pcd_qualify_temp;

drop table t_agent_pcd_qualify_temp;

