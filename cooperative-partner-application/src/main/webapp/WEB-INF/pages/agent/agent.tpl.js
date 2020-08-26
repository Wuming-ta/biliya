    <script type="text/html" id="pcd-select-template">
        <option value="#value#">#name#</option>
    </script>

    <script type="text/html" id="pcd-span-template">
        <div class="pcd-span" onmouseenter="deleteBtnVisible(this);" onmouseleave="deleteBtnHidden(this);">
            <input type="hidden" value="#id#" name="pcdQualityId">
            <input type="hidden" value="#id#-#agent_physical_settlement_percentage#"  name="agentPhysicalSettlementPercentage" />
            <span class="pcd-box">#pcd# #agent_physical_settlement_percentage#</span><span class="pcd-box-delete"><a href="#" onclick="removeFileLi(this);" class="glyphicon glyphicon-trash"></a></span>
        </div>
    </script>