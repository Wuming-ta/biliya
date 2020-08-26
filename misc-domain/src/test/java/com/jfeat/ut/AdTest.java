/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.ut;

import com.jfeat.AbstractTestCase;
import com.jfeat.misc.model.Ad;
import com.jfeat.misc.model.AdGroup;
import org.junit.Test;

import java.util.List;

/**
 * Created by jackyhuang on 16/11/2.
 */
public class AdTest extends AbstractTestCase {

    @Test
    public void testAdCarouselStrategy() {
        AdGroup adGroup = new AdGroup();
        adGroup.setName("abc");
        adGroup.save();

        Ad ad = new Ad();
        ad.setGroupId(adGroup.getId());
        ad.setName("a");
        ad.setStrategy("EVERY_DAY&ODD_DAY&ODD_HOUR");
        ad.setEnabled(Ad.ENABLED);
        ad.save();

        Ad ad2 = new Ad();
        ad2.setGroupId(adGroup.getId());
        ad2.setName("b");
        ad2.setStrategy("EVEN_DAY&ODD_HOUR");
        ad2.setEnabled(Ad.ENABLED);
        ad2.save();

        Ad c = new Ad();
        c.setGroupId(adGroup.getId());
        c.setName("c");
        c.setStrategy("EVEN_DAY&EVEN_HOUR");
        c.setEnabled(Ad.ENABLED);
        c.save();

        Ad d = new Ad();
        d.setGroupId(adGroup.getId());
        d.setName("d");
        d.setStrategy("ODD_HOUR");
        d.setEnabled(Ad.ENABLED);
        d.save();

        Ad e = new Ad();
        e.setGroupId(adGroup.getId());
        e.setName("e");
        e.setStrategy("EVEN_HOUR");
        e.setEnabled(Ad.ENABLED);
        e.save();

        Ad f = new Ad();
        f.setGroupId(adGroup.getId());
        f.setName("f");
        f.setStrategy("ODD_DAY&ODD_HOUR");
        f.setEnabled(Ad.ENABLED);
        f.save();

        List<Ad> list = Ad.dao.findAvailable(adGroup.getId());
        for (Ad a : list) {
            System.out.println(a);
        }
    }
}
