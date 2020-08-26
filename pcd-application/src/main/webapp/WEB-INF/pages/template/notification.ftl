<#macro notification>
    <style>
    @keyframes notification-bg {
        0%   {background:white;}
        100% {background:#428bca;}
    }
    .notification {
        animation: notification-bg 1s infinite;
        -moz-animation: notification-bg 1s infinite; /* Firefox */
        -webkit-animation: notification-bg 1s infinite; /* Safari and Chrome */
        -o-animation: notification-bg 1s infinite; /* Opera */
    }
    </style>
    <#list notifications as n>
      <a class="btn btn-primary btn-sm" <#if n.url??>href="${base}${n.url}"</#if> title="${n.title!}">
        <#if n.display_mode==0>${n.name!}</#if>
        <#if n.display_mode==1>${n.icon!}</#if>
        <#if n.badge_url??>
            <span class="badge notify notification" style="color: #fff" data-badge-url="${base}${n.badge_url!}" data-timeout="${n.timeout!}"></span>
        </#if>
      </a>
    </#list>
</#macro>
