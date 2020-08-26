<script type="text/html" id="province-template">
    <tr>
        <td>#name#</td>
        <td>
            <input type="hidden" name="pcdQualify[#index#].id" value="#index#" />
            <input type="number" class="form-control digit" name="pcdQualify[#index#].physical_settlement_percentage" value="#physical_settlement_percentage#" />
        </td>
        <td>
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
            <input type="number" class="form-control digit" name="pcdQualify[#index#].physical_settlement_percentage" value="#physical_settlement_percentage#" />
        </td>
        <td>
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
            <input type="number" class="form-control digit" name="pcdQualify[#index#].physical_settlement_percentage" value="#physical_settlement_percentage#" />
        </td>
    </tr>
</script>