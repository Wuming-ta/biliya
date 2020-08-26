<script type="text/html" id="province-template">
    <tr>
        <td>#name#</td>
        <td>
            <input type="hidden" name="pcdQualify[#index#].id" value="#index#" />
            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal" data-pcd-id="#pcd_id#">设置</button>
            <button type="button" class="province-btn" value="#index#">
                <i class="fa fa-angle-double-right" aria-hidden="true"></i>
            </button>
        </td>
    </tr>
</script>

<script type="text/html" id="city-template">
    <tr>
        <td>#name#</td>
        <td>
            <input type="hidden" name="pcdQualify[#index#].id" value="#index#" />
            <button type="button" class="btn btn-primary"  data-toggle="modal" data-target="#myModal" data-pcd-id="#pcd_id#">设置</button>
            <button type="button" class="city-btn" value="#index#">
                <i class="fa fa-angle-double-right" aria-hidden="true"></i>
            </button>
        </td>
    </tr>
</script>

<script type="text/html" id="district-template">
    <tr>
        <td>#name#</td>
        <td>
            <input type="hidden" name="pcdQualify[#index#].id" value="#index#" />
            <button type="button" class="btn btn-primary"  data-toggle="modal" data-target="#myModal" data-pcd-id="#pcd_id#">设置</button>
        </td>
    </tr>
</script>

<script type="text/html" id="physical-agent-bonus-template">
    <tr class='physical-agent-bonus'>
        <td>
            <input type="number" class="form-control required" value="#min_amount#" name="physicalAgentBonus[#bonus_id#].min_amount" placeholder="请输入最小值"/> ~
            <input type="number" class="form-control required" value="#max_amount#" name="physicalAgentBonus[#bonus_id#].max_amount" placeholder="请输入最大值"/>
        </td>
        <td>
            <input type="number" class="form-control required" value="#percentage#" name="physicalAgentBonus[#bonus_id#].percentage" placeholder="请输入奖金比例"/>
        </td>
        <td>
            <a class="btn btn-danger" onclick="removePhysicalAgentBonus(this);">删除</a>
        </td>
    </tr>
</script>