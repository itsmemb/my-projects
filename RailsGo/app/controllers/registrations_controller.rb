class RegistrationsController < ApplicationController
    def new
        @user = User.new
    end

    def create
        #render plain: params
        #render plain: params[:user]
        #render plain: "You data is MINE!!!"
        @user = User.new(user_params)
        if @user.save
            # this is bad as user_id could easily be changed
            #cookies[:user_id]
            # so we use session cookies to sogn in the user!
            #server side it can be messed with but browser side it cannto be messed with
            session[:user_id] = @user.id
            redirect_to root_path, notice: "Account created"
        else 
            render :new, status: :unprocessable_entity
        end
    end

    # private

    def user_params
        params.require(:user).permit(:email, :password, :password_confirmation)
    end
end