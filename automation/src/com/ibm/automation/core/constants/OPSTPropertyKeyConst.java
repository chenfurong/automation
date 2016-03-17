package com.ibm.automation.core.constants;

public class OPSTPropertyKeyConst {
	
	public static final String AMS2_HOST = "AMS2_HOST";
	public static final String POST_ams2_service_cmd = "POST_ams2_service_cmd";
	public static final String POST_ams2_service_run = "POST_ams2_service_run";
	public static final String POST_ams2_service_check="POST_ams2_service_check";
	public static final String POST_ams2_service_dbops="POST_ams2_service_dbops";
	public static final String POST_ams2_service_db2_vers="POST_ams2_service_db2_vers";
	public static final String NOVA_HOST = "NOVA_HOST";	
	public static final String NOVA_HOST_SITE = "NOVA_HOST_SITE";
	public static final String IMAGIC_HOST = "IMAGIC_HOST";
	public static final String NETWORK_HOST = "NETWORK_HOST";
	public static final String NETWORK_HOST_SITE = "NETWORK_HOST_SITE";
//	public static final String ADMIN_TENANTID = "ADMIN_TENANTID";
	public static final String VOLUME_ONE = "VOLUME_ONE";
	public static final String VOLUME_TWO = "VOLUME_TWO";
	public static final String VOLUME_TWO_SITE = "VOLUME_TWO_SITE";
	public static final String METERING = "METERING";
	public static final String EC_TWO = "EC_TWO";
	public static final String IDENTITY = "IDENTITY";
	public static final String IDENTITY_PORT = "IDENTITY_PORT";
	
	public static final String CEILOMETER = "CEILOMETER";
	//Auto_register_server
	public static final String AUTO_REGISTER_SERVER = "Auto_register_server";
	
	//openstack host 
	public static final String OPENSTACK_HOST = "OPENSTACK_HOST";
	
	//openstack AuthHost 登陆信息
	public static final String OPENSTACK_AUTHHOST = "OPENSTACK_AUTHHOST";
	public static final String OPENSTACK_USERNAME = "OPENSTACK_USERNAME";
	public static final String OPENSTACK_PASSWORD = "OPENSTACK_PASSWORD";
	
	//认证
	public static final String POST_TOKENAUTH = "POST_tokenAuth";
	public static final String POST_AUTHTOKENS = "POST_authTokens";
	
	public static final String METHOD_ERROR = "method_error";	//调用API返回错误
	
	public static final String GET_VERSIONS = "GET_VERSIONS";

	public static final String GET_ExtensionsList = "GET_ExtensionsList";
	public static final String GET_ExtensionsDetails = "";

	public static final String GET_LimitsList = "GET_LimitsList";

	public static final String GET_ServersList = "GET_ServersList";
	public static final String POST_Servers = "POST_Servers";
	public static final String GET_ServersListDetails = "GET_ServersListDetails";
	public static final String GET_OsAvailabilityZone = "GET_OsAvailabilityZone";
	public static final String GET_OsAvailabilityZoneDetail = "GET_OsAvailabilityZoneDetail";
	public static final String GET_ServersDetails = "GET_ServersDetails";
	public static final String PUT_Servers = "PUT_Servers";
	public static final String DELETE_Servers = "DELETE_Servers";
	public static final String GET_NetworkIdByServerId = "GET_NetworkIdByServerId";
	public static final String GET_OS_Services = "GET_OS_Services";
	public static final String GET_Network_agents = "GET_Network_agents";

	public static final String GET_ServerMetadata = "GET_ServerMetadata";
	public static final String PUT_ServerMetadata = "PUT_ServerMetadata";
	public static final String POST_ServerMetadata = "POST_ServerMetadata";
	public static final String GET_ServerMetadataDetails = "GET_ServerMetadataDetails";
	public static final String PUT_ServerMetadataDetails = "PUT_ServerMetadataDetails";
	public static final String DELETE_ServerMetadataDetails = "DELETE_ServerMetadataDetails";
	public static final String GET_ServerAddressesList = "GET_ServerAddressesList";
	public static final String GET_ServerAddressesDetails = "GET_ServerAddressesDetails";

	public static final String POST_ServerActionsChangePassword = "POST_ServerActionsChangePassword";
	public static final String POST_ServerActionsReboot = "POST_ServerActionsReboot";
	public static final String POST_ServerActionsRebuild = "POST_ServerActionsRebuild";
	public static final String POST_ServerActionsResize = "POST_ServerActionsResize";
	public static final String POST_ServerActionsConfirmResize = "POST_ServerActionsConfirmResize";
	public static final String POST_ServerActionsRevertResize = "POST_ServerActionsRevertResize";
	public static final String POST_ServerActionsCreateImage = "POST_ServerActionsCreateImage";
	
	
	public static final String GET_TENANTUSAGEDETAIL = "GET_TenantUsageDetail";
	//v2 ext
	public static final String GET_HYPERVISORLIST = "GET_hypervisorList";
	public static final String GET_HYPERVISORDETAIL = "GET_hypervisorDetail";
	public static final String GET_HYPERVISOR_STATISTICS = "GET_hypervisor_statistics";

	public static final String GET_FlavorsList = "GET_FlavorsList";
	public static final String GET_FlavorsListDetails = "GET_FlavorsListDetails";
	public static final String GET_FlavorsDetails = "GET_FlavorsDetails";
	public static final String POST_Flavors = "POST_FLAVORS";
	public static final String DELETE_Flavors = "DELETE_FLAVORS";
	public static final String FLAVOR_ACCESS_LIST = "FLAVOR_ACCESS_LIST";
	public static final String FLAVOR_ACCESS_OPERATE = "FLAVOR_ACCESS_OPERATE";
	
	public static final String GET_FlavorsExtList = "GET_FlavorsListDetailsExtraData";
	public static final String GET_FlavorsExtDetails = "GET_FlavorsExtDetails";
	public static final String GET_FlavorExtraSpecs = "GET_FlavorExtraSpecs";

	public static final String GET_ImagesList = "GET_ImagesList";
	public static final String GET_ImagesListDetails = "GET_ImagesListDetails";
	public static final String GET_ImagesDetails = "GET_ImagesDetails";
	//
	public static final String GET_ImagesDetailsV1 = "GET_ImagesDetailsV1";
	public static final String DELETE_Images = "DELETE_Images";
	public static final String POST_createImage = "POST_createImage";
	public static final String PUT_updateImageById = "PUT_updateImageById";
	
	public static final String GET_IMAGEMENBERLIST="GET_imageMenberList";
	public static final String GET_IMAGEMENBERBYID="GET_imageMenberById";
	public static final String PUT_CREATEIMAGEMENBER="PUT_createImageMenber";
	public static final String DELETE_DELETEIMAGEMENBER="DELETE_deleteImageMenber";

	public static final String DELETE_ImageMetadata = "DELETE_ImageMetadata";
	public static final String PUT_ImageMetadata = "PUT_ImageMetadata";
	public static final String POST_ImageMetadata = "POST_ImageMetadata";
	public static final String GET_ImageMetadataDetails = "GET_ImageMetadataDetails";
	public static final String PUT_ImageMetadataDetails = "PUT_ImageMetadataDetails";
	public static final String DELETE_ImageMetadataDetails = "DELETE_ImageMetadataDetails";
	
	//网络
	//获取网络信息列表
	public static final String GET_NETWORK_LIST = "GET_NetworkList";
	//获取网络信息
	public static final String GET_NETWORKBYID = "GET_NetwrokById";
	//创建网络
	public static final String POST_CREATENETWORK = "POST_CreateNetwork";
	//创建多网络
	public static final String POST_CREATEMULTINETWORK = "POST_CreateMultiNetwork";
	//更新网络信息
	public static final String PUT_UPDATENETWORKBYID = "PUT_UpdateNetworkById";
	//删除网络
	public static final String DELETE_NETWORK = "DELETE_NetworkById";
	
	//子网
	//获取子网信息列表
	public static final String GET_SUBNETLIST = "GET_SubnetList";
	//获取子网信息
	public static final String GET_SUBNETBYID = "GET_SubnetById";
	//创建子网
	public static final String POST_CREATESUBNET = "POST_CreateSubnet";
	//创建多子网
	public static final String POST_CREATEMULTISUBNET = "POST_CreateMultiSubnet";
	//更新子网信息
	public static final String PUT_UPDATESUBNETBYID = "PUT_UpdateSubnetById";
	//删除子网
	public static final String DELETE_SUBNETBYID = "DELETE_SubnetById";
	
	//端口
	//获取端口信息列表
	public static final String GET_PORTLIST = "GET_PortList";
	//获取端口信息
	public static final String GET_PORTBYID = "GET_PortById";
	//创建端口
	public static final String POST_CREATEPORT = "POST_CreatePort";
	//创建多端口
	public static final String POST_CREATEMULTIPORT = "POST_CreateMultiPort";
	//更新端口信息
	public static final String PUT_UPDATEPORTBYID = "PUT_UpdatePortById";
	//删除端口
	public static final String DELETE_PORTBYID = "DELETE_PortById";
	
	//安全组
	//获取安全组信息列表
	public static final String GET_SECURITYGROUPLIST = "GET_SecurityGroupList";
	//获取安全组信息
	public static final String GET_SECURITYGROUPBYID = "GET_SecurityGroupById";
	//创建安全组
	public static final String POST_CREATESECURITYGROUP = "POST_CreateSecurityGroup";
	//删除安全组
	public static final String DELETE_SECURITYGROUPBYID = "DELETE_SecurityGroupById";
	//修改
	public static final String PUT_SECURITYGROUPBYID = "PUT_SecurityGroupById";
	
	//安全组规则  security group rules
	public static final String GET_SECURITYGROUPRULELIST = "GET_securityGroupRuleList";
	public static final String POST_CREATESECURITYGROUPRULE = "POST_createSecurityGroupRule";
	public static final String GET_SECURITYGROUPRULEBYID = "GET_securityGroupRuleById"; 
	public static final String DELETE_SECURITYGROUPRULEBYID = "DELETE_securityGroupRuleById";
	
	//VOLUMES
	public static final String GET_VOLUME_LIST = "GET_VolumeList";
	public static final String GET_VOLUME_DETAIL_LIST = "GET_VolumeDetailList";
	public static final String GET_VOLUMEBYID ="GET_VolumeById";
	public static final String POST_CREATEVOLUME = "POST_CreateVolume";
	public static final String PUT_UPDATEVOLUMESBYID = "PUT_UpdateVolumesById";
	public static final String DELETE_VOLUMESBYID = "DELETE_VolumesById";
	//数据盘  租户配额
	public static final String GET_VOLUMETENANTQUOTAS = "GET_volumeTenantQuotas";
	//挂载数据盘
	public static final String POST_CREATEVOLUMEMOUNT = "POST_CreateVolumeMount";	
	//分离数据盘
	public static final String DELETE_VOLUMESSEPARATEBYID = "DELETE_VolumesSeparateById";
	public static final String GET_SERVERVOLUMESBYID = "GET_ServerVolumesById";
	
	//VOLUME TYPES
	public static final String GET_VOLUMETYPE_LIST = "GET_VolumeTypeList";
	public static final String GET_VOLUMETYPEBYID = "GET_VolumeTypeById";
	
	//OverAllocation
	public static final String OVER_ALLOCATION_KVM = "Over_Allocation_kvm";
	public static final String OVER_ALLOCATION_VMVARE = "Over_Allocation_vmvare";
	public static final String OVER_ALLOCATION_POWERVM = "Over_Allocation_powervm";
	 
	//SNAPSHOTS
	public static final String GET_SNAPSHOT_LIST = "GET_SnapshotList";
	public static final String GET_SNAPSHOT_DETAIL_LIST = "GET_SnapshotDetailList";
	public static final String GET_SNAPSHOTBYID = "GET_SnapshotById";
	public static final String POST_CREATESNAPSHOT = "POST_CreateSnapshot";
	public static final String PUT_UPDATESNAPSHOTBYID = "PUT_UpdateSnapshotById";
	public static final String DELETE_SNAPSHOTBYID = "DELETE_SnapshotById";
	public static final String GET_SNAPSHOT_METADATABYID = "GET_SnapshotMetadataById";
	public static final String PUT_UPDATE_SNAPSHOT_METADATABYID = "PUT_UpdateSnapshotMetadataById";
	
	//alarms
	public static final String GET_ALARMLIST="GET_AlarmList";
	public static final String GET_ALARMBYID="GET_AlarmById";
	public static final String POST_CREATEALARM="POST_CreateAlarm";
	public static final String PUT_UPDATEALARM="PUT_UpdateAlarm";
	public static final String DELETE_ALARMBYID="DELETE_AlarmById";
	public static final String GET_ALARM_STATE="GET_Alarm_state";
	public static final String PUT_ALARM_STATE="PUT_Alarm_state";
	public static final String GET_ALARM_HISTORY="GET_Alarm_history";

	//meters
	public static final String GET_meterList="GET_meterList";
	public static final String GET_meterByName="GET_meterByName";
	public static final String POST_meterByName="POST_meterByName";
	public static final String GET_meter_statistics="GET_meter_statistics";

	//Resources
	public static final String GET_resourceList="GET_resourceList";
	public static final String GET_resourceById="GET_resourceById";
	
	//keypairs
	public static final String GET_KEYPAIRS="GET_keyPairs";
	public static final String POST_KEYPAIRS="POST_keyPairs";
	public static final String DELETE_KEYPAIRS="DELETE_keyPairs";
	
	//Routers
	public static final String GET_RouterList = "GET_RouterList" ;
	public static final String POST_CreateRouter = "POST_CreateRouter";
	public static final String GET_RouterDetails = "GET_RouterDetails";
	public static final String PUT_UpdateRouter = "PUT_UpdateRouter";
	public static final String DELETE_DeleteRouter = "DELETE_DeleteRouter";
	
	//Interface
	public static final String PUT_AddRouterInterface = "PUT_AddRouterInterface";
	public static final String PUT_RemoveRouterInterface = "PUT_RemoveRouterInterface";
	
	//FloatingIp
	public static final String GET_FloatingIpList = "GET_FloatingIpList";
	public static final String POST_AllocateFloatingIp = "POST_AllocateFloatingIp";
	public static final String DELETE_DeallocateFloatingIp = "DELETE_DeallocateFloatingIp";
	public static final String GET_FloatingIpDetails = "GET_FloatingIpDetails";
	public static final String POST_AddFloatingIpToInstance = "POST_AddFloatingIpToInstance";
	public static final String POST_RemoveFloatingIpFromInstance = "POST_RemoveFloatingIpFromInstance";
	
	//Project
	public static final String GET_PROJECTLIST = "GET_ProjectList";
	public static final String GET_PROJECTDETAIL = "GET_ProjectDetail";
	public static final String POST_PROJECTADD = "POST_ProjectAdd";
	public static final String PATCH_PROJECTUPDATEBYID = "PATCH_ProjectUpdateById";
	public static final String DELETE_PROJECTDELETEBYID = "DELETE_ProjectDeleteById";
	//Lists roles for a user in a project.
	public static final String GET_PROJECTSBYUSERROLELIST = "GET_ProjectsByUserRoleList";
	//Grants a role to a user on a project. 
	public static final String PUT_PROJECTSBYUSERROLE_GRANTS = "PUT_ProjectsByUserRole_Grants";
	//Validates that a user has a specified role on a project.
	public static final String HEAD_PROJECTSBYUSERROLE_VALIDATES = "HEAD_ProjectsByUserRole_Validates";
	//Revokes a role from a project user. 
	public static final String DELETE_PROJECTSBYUSERROLE_REVOKES = "DELETE_ProjectsByUserRole_Revokes";
	
	
	//user
	public static final String GET_USERLIST="GET_UserList";
	public static final String POST_CREATEUSER="POST_createUser";
	public static final String PUT_UPDATEUSERBYID="PUT_UpdateUserById";
	public static final String DELETE_DELETEUSERBYID="DELETE_deleteUserById";
	public static final String GET_GETUSERBYID="GET_getUserById";
	public static final String GET_GETUSERBYNAME="GET_getUserByName";
	public static final String GET_PROJECTSLISTBYUSERID="GET_projectsListByUserId";
	//Tenants
	public static final String GET_TENANTSLIST="GET_TenantsList";
	public static final String GET_GETTENANTBYID="GET_getTenantById";
	public static final String POST_UPDATETENANTBYID="POST_UpdateTenantById";
	public static final String GET_USERLISTBYTENANTID="GET_userListByTenantId";
	public static final String GET_GETROLESBYUSERID="GET_getRolesByUserId";

	//OS-KSADM roles
	public static final String GET_ROLESLIST="GET_rolesList";
	//Adds a specified role to a user for a tenant. 
	public static final String PUT_USERROLEADD="PUT_USERRoleAdd";
	public static final String DELETE_DELETESUSERROLE="DELETE_deletesUserRole";
	//Quotas
	public static final String GET_QuotasList = "GET_QuotasList";
	
	public static final String GET_TENANTQUOTASBYTENANTID = "GET_tenantQuotasByTenantid";
	public static final String POST_TENANTQUOTASUPDATE ="POST_tenantQuotasUpdate";
	public static final String DELETE_TENANTQUOTASDELETE = "DELETE_tenantQuotasDelete";
	
	//实例操作
	public static final String POST_Action = "POST_Action";
	public static final String POST_ActionV3 = "POST_ActionV3";
	
	//LBaas负载均衡
	//VIPs
	public static final String GET_VIP_LIST = "GET_VipList";
	public static final String POST_CREATEVIP = "POST_CreateVip";
	public static final String GET_VIPBYID = "GET_VipById";
	public static final String PUT_UPDATEVIP = "PUT_UpdateVip";
	public static final String DELETE_VIPBYID = "DELETE_VipById";
	//HealthMonitor
	public static final String GET_HealthMonitorList = "GET_HealthMonitorList";
	public static final String POST_CreateHealthMonitor = "POST_CreateHealthMonitor";
	public static final String GET_HealthMonitorDetails = "GET_HealthMonitorDetails";
	public static final String PUT_UpdateHealthMonitor = "PUT_UpdateHealthMonitor";
	public static final String DELETE_DeleteHealthMonitor = "DELETE_DeleteHealthMonitor";
	//Pool
	public static final String GET_PoolList = "GET_PoolList";
	public static final String POST_CreatePool = "POST_CreatePool";
	public static final String GET_PoolDetails = "GET_PoolDetails";
	public static final String PUT_UpdatePool = "PUT_UpdatePool";
	public static final String DELETE_DeletePool = "DELETE_DeletePool";
	public static final String POST_AssociatesHealthMonitorToPool = "POST_AssociatesHealthMonitorToPool";
	public static final String DELETE_DisassociatesHealthMoniterFromPool = "DELETE_DisassociatesHealthMoniterFromPool";
	//Member
	public static final String GET_MEMBER_LIST = "GET_MemberList";
	public static final String POST_CreateMember = "POST_CreateMember";
	public static final String GET_MEMBERBYID = "GET_MemberById";
	public static final String PUT_UpdateMember = "PUT_UpdateMember";
	public static final String DELETE_MEMBERBYID = "DELETE_MemberById";
	
	//netWork kvm  vmvare  power.
	public static final String NETWORK_KVM="NetWork_kvm";
	public static final String NETWORK_VMVARE="NetWork_vmvare";
	public static final String NETWORK_POWERVM="NetWork_powervm";
	//Default hypersor
	public static final String DEFAULT_HYPERVISOR="Default_hypervisor"; 
	//Image_powervm
	public static final String IMAGES_POWERVM="Images_powervm";
	//Instance_Protect
	public static final String INSTANCE_PROTECT="Instance_Protect";
	//volume_protect
	public static final String VOLUME_TYPES_KVM="Volume_types_kvm";
	public static final String VOLUME_TYPES_VMVARE="Volume_types_vmvare";
	public static final String VOLUME_TYPES_POWERVM="Volume_types_powervm";
	//Flavors_protect
	public static final String FLAVORS_PROTECT="Flavors_protect";
			
	//Ceilometer
	public static final String GET_CEILOMETER_METER ="GET_CEILOMETER_METER";	
}