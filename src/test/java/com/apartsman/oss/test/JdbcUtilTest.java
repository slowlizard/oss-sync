package com.apartsman.oss.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.apartsman.service.sync.JdbcUtil;

public class JdbcUtilTest {

	@Test
	public void test() {
		boolean  isNew=JdbcUtil.find("/aparts/img/OrderDvidence/11/1446005164000_1.jpg");
		assertTrue(isNew);
		
	}

}
