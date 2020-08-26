INSERT INTO t_coupon_template (name, type, is_limited, is_discount, cond) VALUES ('拼团活动免单券', 'MARKETING_PIECE_GROUP', 0, 2, '
<rule-set name="getFinalPrice" >
    <mvel-rule id="step1" multipleTimes="false" exclusive="true" valid="true">
        <condition><![CDATA["MARKETING_PIECE_GROUP".equals(type)]]></condition>
        <action><![CDATA[finalPrice=0.0]]></action>
    </mvel-rule>
</rule-set>
');
