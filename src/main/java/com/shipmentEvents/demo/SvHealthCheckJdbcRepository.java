

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;



@Service
public class SvHealthCheckJdbcRepository implements HealthCheckServiceInterface {
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Override
	public SvHealthCheck findById(String svid) {
	    return jdbcTemplate.queryForObject("select * from svhealthcheck where svid=?", new Object[] {
	            svid
	        },
	        new BeanPropertyRowMapper < SvHealthCheck > (SvHealthCheck.class));
	}
	
	@Override
	public boolean svIdExists(String svid) {
	    int count= jdbcTemplate.queryForObject("select count(*) from svhealthcheck where svid=?", new Object[] {
	            svid
	        },
	      Integer.class);
	    
	    return count > 0;
	}
	
	
	@Override
	public List < SvHealthCheck > findAll() {
	    return jdbcTemplate.query("select * from svhealthcheck", new SvHealthCheckRowMapper());
	}
	
	@Override
public int deleteById(String id) {
    return jdbcTemplate.update("delete from svhealthcheck where svid=?", new Object[] {
        id
    });
}
	@Override
public int insert(SvHealthCheck svHealthCheck) {
    return jdbcTemplate.update("insert into svhealthcheck (svid, svclientname, svenv, svip, svstatus, svup, svrelease, svtag, svcompile) "
+ "values(?,  ?, ?, ?, ?, ?, ?, ?, ?)",
        new Object[] {
        		svHealthCheck.getSvid(), svHealthCheck.getSvclientname(), svHealthCheck.getSvenv(), svHealthCheck.getSvip(),
        		svHealthCheck.getSvstatus(), svHealthCheck.isSvup(), svHealthCheck.getSvrelease(), svHealthCheck.getSvtag(),
        		svHealthCheck.getSvcompile()
        });
}


	@Override
public int update(SvHealthCheck svHealthCheck) {
//    return jdbcTemplate.update("update svhealthcheck " + " set svclientname = ?, svenv = ?, svip = ?, svstatus = ?"
//    		+ " svup = ?, svrelease = ?, svtag = ?, svcompile = ? " + " where svid = ?",
//        new Object[] {
//        		svHealthCheck.getSvclientname(), svHealthCheck.getSvenv(), svHealthCheck.getSvip(),
//        		svHealthCheck.getSvstatus(), svHealthCheck.isSvup(), svHealthCheck.getSvrelease(), svHealthCheck.getSvtag(),
//        		svHealthCheck.getSvcompile(),svHealthCheck.getSvid()
//        });
//    
		
	    return jdbcTemplate.update("update svhealthcheck " + " set svclientname = ?, svenv = ?, svip = ?, svstatus = ?,"
	    		+ " svup = ?, svrelease = ?, svtag = ?, svcompile = ? " + " where svid = ?",svHealthCheck.getSvclientname(), svHealthCheck.getSvenv(),
	    		svHealthCheck.getSvip(), svHealthCheck.getSvstatus(),svHealthCheck.isSvup(),svHealthCheck.getSvrelease(), svHealthCheck.getSvtag(),
	    		svHealthCheck.getSvcompile(),svHealthCheck.getSvid());
	    
   
    
}

}


class SvHealthCheckRowMapper implements RowMapper < SvHealthCheck > {
    @Override
    public SvHealthCheck mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
    
    	SvHealthCheck svHealthChk = new SvHealthCheck();
    	svHealthChk.setSvid(rs.getString("svid"));
    	svHealthChk.setSvclientname(rs.getString("svclientname"));
    	svHealthChk.setSvenv(rs.getString("svenv"));
    	svHealthChk.setSvip(rs.getString("svip"));
    	svHealthChk.setSvstatus(rs.getInt("svstatus"));
    	svHealthChk.setSvup(rs.getBoolean("svup"));
    	svHealthChk.setSvrelease(rs.getString("svrelease"));
    	svHealthChk.setSvtag(rs.getString("svtag"));
    	svHealthChk.setSvcompile(rs.getString("svcompile"));
        return svHealthChk;
    }
}
