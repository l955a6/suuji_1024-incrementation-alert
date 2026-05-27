{
  description = "development environment for suuji_1024-incrementation-alert";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    { nixpkgs, flake-utils, ... }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
        jdk = pkgs.jdk25;
        sbt = pkgs.sbt.override { jre = jdk; };
      in
      {
        devShells = {
          default = pkgs.mkShell {
            buildInputs = [
              jdk
              sbt
              pkgs.metals
            ];

            shellHook = ''
              echo "Welcome to the suuji_1024-incrementation-alert development environment!🤏"
            '';
          };
        };
      }
    );
}
