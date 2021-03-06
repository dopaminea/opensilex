/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.security.group.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.opensilex.security.group.dal.GroupUserProfileModel;
import org.opensilex.security.profile.dal.ProfileModel;
import org.opensilex.security.user.dal.UserModel;
import org.opensilex.server.rest.validation.ValidURI;
import org.opensilex.sparql.response.ResourceDTO;

/**
 *
 * @author vidalmor
 */
@ApiModel
public class GroupUserProfileDTO extends ResourceDTO<GroupUserProfileModel> {

    protected URI profileURI;

    protected String profileName;

    protected URI userURI;

    protected String userName;

    @ValidURI
    @ApiModelProperty(value = "Group URI", example = "http://opensilex.dev/groups#Experiment_manager")
    public URI getUri() {
        return uri;
    }

    @ValidURI
    @ApiModelProperty(value = "User associated profile URI")
    public URI getProfileURI() {
        return profileURI;
    }

    public void setProfileURI(URI profileURI) {
        this.profileURI = profileURI;
    }

    @ApiModelProperty(value = "User associated profile name")
    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    @ValidURI
    @ApiModelProperty(value = "User URI")
    public URI getUserURI() {
        return userURI;
    }

    public void setUserURI(URI userURI) {
        this.userURI = userURI;
    }

    @ApiModelProperty(value = "User name")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void toModel(GroupUserProfileModel model) {
        super.toModel(model);

        ProfileModel profile = new ProfileModel();
        profile.setUri(getProfileURI());
        profile.setName(getProfileName());
        model.setProfile(profile);

        UserModel user = new UserModel();
        user.setUri(userURI);
        model.setUser(user);
    }

    @Override
    public void fromModel(GroupUserProfileModel model) {
        super.fromModel(model);

        setProfileURI(model.getProfile().getUri());
        setProfileName(model.getProfile().getName());

        setUserURI(model.getUser().getUri());
        setUserName(model.getUser().getName());
    }

    @Override
    public GroupUserProfileModel newModelInstance() {
        return new GroupUserProfileModel();
    }
}
