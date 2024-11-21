{
  description = "A basic flake supporting linux and mac";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-24.05";
  };

  outputs = { self, nixpkgs, ... }:
    let
      lib = nixpkgs.lib;
      systems = [ "aarch64-linux" "x86_64-linux" "x86_64-darwin" ];
      devShellForSystem = system: let
        pkgs = import nixpkgs { 
          inherit system; 
          config.allowUnfree = true; # Needed for terraform and other "unfree" packages
        };
      in pkgs.mkShell {
        buildInputs = with pkgs; [
          zsh
          jdk8
          scenebuilder
          # nodejs_20
          # htop
          # python3
          # docker
          # azure-cli
          # terraform
          # dotnet-sdk_8
          # neo-cowsay
          # gh
        ];
      };
    in
    {
      devShells = lib.listToAttrs (map (system: {
        name = system;
        value = {
          default = devShellForSystem system;
        };
      }) systems);
    };
}
